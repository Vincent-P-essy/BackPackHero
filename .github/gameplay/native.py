"""Drive real game windows with X11 keyboard/mouse and capture their pixels."""
import json
import os
from pathlib import Path
import random
import runpy
import sqlite3
import subprocess
import sys
import threading
import time
from PIL import ImageGrab

OUT = Path('docs/screenshots')
OUT.mkdir(parents=True, exist_ok=True)
repo = os.environ['GAME_REPO']

def xdo(*args):
    return subprocess.check_output(['xdotool', *map(str, args)], text=True).strip()

def wait_for(predicate, seconds=30):
    deadline = time.monotonic() + seconds
    while time.monotonic() < deadline:
        result = predicate()
        if result:
            return result
        time.sleep(.2)
    raise AssertionError('Game action did not complete')

def window(title):
    def find():
        proc = subprocess.run(['xdotool', 'search', '--onlyvisible', '--name', title], capture_output=True, text=True)
        return proc.stdout.splitlines()[0] if proc.returncode == 0 else None
    win = wait_for(find)
    xdo('windowactivate', '--sync', win)
    xdo('windowmove', win, 0, 0)
    return win

def shot(name, win=None):
    if win is None:
        box = (0, 0, 1024, 768)
    else:
        fields = dict(line.split('=', 1) for line in xdo('getwindowgeometry', '--shell', win).splitlines())
        x, y, w, h = [int(fields[k]) for k in ['X', 'Y', 'WIDTH', 'HEIGHT']]
        box = (x, y, x+w, y+h)
    ImageGrab.grab(bbox=box).save(OUT / (name + '.png'))

def report(data):
    Path('docs/gameplay.json').write_text(json.dumps({'played': True, **data}, indent=2) + '\n')

def send(text):
    xdo('type', '--clearmodifiers', '--delay', 100, text)
    xdo('key', 'Return')
    time.sleep(.5)

def play_pythia():
    save = '/tmp/pythia-gameplay.sqlite3'
    proc = subprocess.Popen(['xterm', '-T', 'Pythia', '-geometry', '100x42+0+0', '-fa', 'DejaVu Sans Mono', '-fs', '12',
        '-e', 'pythia', 'play', '--seed', '12345', '--narrator', 'offline', '--save', save])
    try:
        win = window('^Pythia$')
        time.sleep(3)
        actions = ['l', 'l', 'j', 'j', 'g', 'k', 'h', 'h', 'g', 'period', 'period', 's']
        for key in actions:
            xdo('key', '--clearmodifiers', key)
            time.sleep(.25)
        wait_for(lambda: Path(save).exists())
        with sqlite3.connect(save) as connection:
            state = json.loads(connection.execute("SELECT payload FROM saves WHERE slot='default'").fetchone()[0])
        assert state['turn'] >= 5, f"No gameplay turns: {state['turn']}"
        shot('gameplay', win)
        xdo('key', 'i'); time.sleep(.7)
        shot('gameplay-inventory', win)
        report({'interface': 'Textual TUI', 'narrator': 'offline', 'seed': state['seed'], 'turn': state['turn'],
            'player': {k: state['player'][k] for k in ['x', 'y', 'hp', 'gold']}, 'actions': actions})
        xdo('key', 'q')
        proc.wait(timeout=10)
    finally:
        if proc.poll() is None: proc.terminate()

def play_darkest():
    transcript = Path('/tmp/darkest-gameplay.txt')
    proc = subprocess.Popen(['xterm', '-T', 'Darkest C Dungeon', '-geometry', '90x35+0+0', '-fa', 'DejaVu Sans Mono',
        '-fs', '14', '-bg', '#11151b', '-fg', '#e3e9ef', '-e', 'script', '-q', '-f', '-c', './game', str(transcript)])
    def text():
        return transcript.read_text(errors='replace') if transcript.exists() else ''
    try:
        win = window('^Darkest C Dungeon$')
        time.sleep(1)
        for answer in ['1', '1', '1']:
            send(answer)
        wait_for(lambda: '=== Tour 1 ===' in text())
        send('D'); send('A')
        wait_for(lambda: '=== Tour 2 ===' in text())
        assert 'inflige' in text() and ("L'ennemi attaque" in text() or "L'ennemi stresse" in text()), text()[-2000:]
        shot('gameplay', win)
        report({'interface': 'original console game', 'turns_reached': 2,
            'actions': ['new game', 'choose two heroes', 'defend', 'attack', 'enemy turn'],
            'combat_observed': True})
        xdo('key', 'ctrl+c')
    finally:
        if proc.poll() is None: proc.terminate()

def play_pieges():
    sys.path.insert(0, str(Path.cwd()))
    import game
    holder = {}
    original = game.Plateau.__init__
    def observe(self, *args, **kwargs):
        original(self, *args, **kwargs)
        holder['board'] = self
    game.Plateau.__init__ = observe
    random.seed(20261002)
    errors = []
    def driver():
        win = None
        try:
            board = wait_for(lambda: holder.get('board'))
            win = window('^tk$')
            time.sleep(2)
            placed = []
            for y in [0, 2, 4, 6, 1, 3, 5]:
                for x in [0, 2, 4, 6, 1, 3, 5]:
                    if board.est_ce_un_trou(x, y) == 2: continue
                    before = len(board.billes)
                    xdo('mousemove', '--window', win, 232+(x+1)*62, 120+(y+1)*62)
                    xdo('click', '1')
                    wait_for(lambda: len(board.billes) > before, 3)
                    placed.append([x, y])
                    if len(placed) == 10: break
                if len(placed) == 10: break
            assert len(placed) == 10
            time.sleep(.5)
            shot('gameplay-placement', win)
            pulls = []
            for index in [0, 3, 5]:
                slider = board.tirettes[0][index]
                before = slider.position
                xdo('mousemove', '--window', win, 230+(index+1)*62, 596+before*30)
                xdo('click', '1')
                wait_for(lambda: slider.position == before+1, 3)
                pulls.append(index)
                if len(board.billes) == 0: break
            time.sleep(.7)
            shot('gameplay', win)
            report({'interface': 'Tkinter GUI', 'placed_marbles': placed, 'pulled_vertical_sliders': pulls,
                'remaining_marbles': len(board.billes), 'seed': 20261002})
        except BaseException as error:
            errors.append(error)
        finally:
            if win is not None: xdo('key', 'Escape')
    thread = threading.Thread(target=driver, daemon=True)
    thread.start()
    runpy.run_path('piege.py', run_name='__main__')
    thread.join(timeout=5)
    if errors: raise errors[0]
    assert Path('docs/gameplay.json').exists()

if repo == 'BackPackHero':
    subprocess.run(['java', '-cp', 'lib/zen-6.0.jar:bin', 'GameplayCapture'], check=True, timeout=90)
elif repo == 'pythia': play_pythia()
elif repo == 'Darkestcdungeon': play_darkest()
elif repo == 'Pieges-': play_pieges()
else: raise ValueError(repo)
