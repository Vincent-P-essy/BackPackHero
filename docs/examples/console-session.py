"""Drive the repository console game through a real pseudo-terminal."""
import os,pty,select,subprocess,sys,time
master,slave=pty.openpty()
process=subprocess.Popen(["java","-cp","bin","Main"],stdin=slave,stdout=slave,stderr=slave)
os.close(slave)
steps=[(b"Choice (1/2):",b"1\n"),(b"> ",b"backpack\n"),(b"> ",b"help\n"),(b"> ",b"quit\n")]
buffer=b""; output=b""; deadline=time.monotonic()+20
try:
    while time.monotonic()<deadline:
        ready,_,_=select.select([master],[],[],.1)
        if ready:
            try:chunk=os.read(master,65536)
            except OSError:break
            if not chunk:break
            output+=chunk;buffer+=chunk
            if steps and steps[0][0] in buffer:
                _,command=steps.pop(0);os.write(master,command);buffer=b""
        elif process.poll() is not None:break
    if process.poll() is None:
        process.kill();raise RuntimeError("Console session timed out")
    sys.stdout.write(output.decode(errors="replace"))
    if steps:raise RuntimeError("The expected game prompts were not reached")
    raise SystemExit(process.returncode)
finally:
    if process.poll() is None:process.kill()
    os.close(master)
