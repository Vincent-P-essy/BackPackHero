import { mkdirSync, writeFileSync } from 'node:fs';
import puppeteer from 'puppeteer';

const repo = process.env.GAME_REPO;
const out = 'docs/screenshots';
mkdirSync(out, { recursive: true });
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));
for (let attempt=0; attempt<30; attempt++) {
  try { const response=await fetch('http://127.0.0.1:4173/'); if(response.ok) break; } catch {}
  await wait(500);
}
const browser = await puppeteer.launch({headless:true,args:['--no-sandbox','--enable-unsafe-swiftshader']});
const observations = {played:false, interface:'browser', actions:[]};
const assert = (condition, message) => { if (!condition) throw new Error(message); };
try {
  const page = await browser.newPage();
  await page.setViewport({width: repo==='tropico2'?1600:1280,height:repo==='tropico2'?900:720});
  await page.goto('http://127.0.0.1:4173/',{waitUntil:'domcontentloaded',timeout:60000});
  if (repo === 'minecraft-clone') {
    await page.waitForFunction(()=>window.__mc?.meshedChunks()>=60,{timeout:120000,polling:500});
    await page.click('#play-button'); await wait(800);
    assert(await page.evaluate(()=>document.pointerLockElement!==null),'Play did not engage pointer lock');
    const look = async (yaw,pitch) => {
      await page.evaluate(([yaw,pitch])=>{
        document.dispatchEvent(new MouseEvent('mousemove',{movementY:-2000}));
        document.dispatchEvent(new MouseEvent('mousemove',{movementX:yaw,movementY:pitch}));
      },[yaw,pitch]); await wait(600);
    };
    const neighbor = await page.evaluate(()=>{
      const p=window.__mc.position(), x=Math.floor(p.x),y=Math.floor(p.y),z=Math.floor(p.z);
      for (const c of [{dx:1,dz:0,yaw:-Math.PI/2},{dx:-1,dz:0,yaw:Math.PI/2},{dx:0,dz:-1,yaw:0},{dx:0,dz:1,yaw:Math.PI}]) {
        const below=window.__mc.blockAt(x+c.dx,y-1,z+c.dz);
        if(below!==0&&below!==5&&window.__mc.blockAt(x+c.dx,y,z+c.dz)===0) return {...c,x:x+c.dx,y:y-1,z:z+c.dz};
      } return null;
    });
    assert(neighbor,'No nearby placement surface');
    await look(-neighbor.yaw/.0022,1146);
    await page.mouse.down({button:'right'}); await page.mouse.up({button:'right'}); await wait(700);
    const placed=await page.evaluate(n=>window.__mc.blockAt(n.x,n.y+1,n.z),neighbor);
    assert(placed===1,'Right click did not place grass');
    await page.screenshot({path:`${out}/gameplay-building.png`});
    await look(neighbor.yaw/.0022,2000);
    const target=await page.evaluate(()=>window.__mc.target()); assert(target,'No mining target');
    await page.mouse.down({button:'left'}); await page.mouse.up({button:'left'}); await wait(700);
    assert(await page.evaluate(t=>window.__mc.blockAt(t.x,t.y,t.z)===0,target),'Left click did not break block');
    await look(0,709);
    const before=await page.evaluate(()=>window.__mc.position());
    await page.keyboard.down('KeyW'); await wait(2500); await page.keyboard.up('KeyW');
    const after=await page.evaluate(()=>window.__mc.position());
    const distance=Math.hypot(after.x-before.x,after.z-before.z);
    assert(distance>1,'Player did not walk');
    await page.screenshot({path:`${out}/gameplay.png`});
    Object.assign(observations,{played:true,actions:['play','look','place grass','mine block','walk forward'],
      placed_at:neighbor,mined_at:target,walked_blocks:distance,player:after});
  } else if(repo==='tropico2') {
    await page.waitForFunction(()=>window.tropico,{timeout:30000});
    await page.click('#start .primary'); await wait(1000);
    await page.click('button[title="Pause"]');
    const read = ()=>page.evaluate(()=>{
      const s=window.tropico.state(); return {buildings:s.buildings.size,tick:s.tick,lumber:s.lumber,treasury:s.treasury};
    });
    const initial=await read();
    const pick = async name => {
      const point=await page.evaluate(name=>{
        const button=[...document.querySelectorAll('button.build-item')].find(b=>b.firstElementChild.textContent===name);
        if(!button) return null;
        button.scrollIntoView(); const r=button.getBoundingClientRect(); return {x:r.x+r.width/2,y:r.y+r.height/2};
      },name); assert(point,`No ${name} control`); await page.mouse.click(point.x,point.y); await wait(100);
    };
    const place = async (name,count) => {
      await pick(name); let added=0;
      for(let y=220;y<690&&added<count;y+=24) {
        for(let x=400;x<1340&&added<count;x+=32) {
          const before=await read(); await page.mouse.click(x,y); await wait(25);
          const after=await read(); if(after.buildings>before.buildings) added+=after.buildings-before.buildings;
        }
      }
      assert(added>=count,`Only ${added}/${count} ${name} placed`);
      await page.keyboard.press('Escape'); return added;
    };
    const roads=await place('Road',3);
    const tents=await place('Construction Tent',1);
    await page.keyboard.press('4'); await wait(8000); await page.keyboard.press('Space'); await wait(500);
    const final=await read();
    assert(final.tick>initial.tick,'Simulation did not advance');
    await page.screenshot({path:`${out}/gameplay.png`});
    await page.keyboard.press('a'); await wait(500);
    await page.screenshot({path:`${out}/gameplay-almanac.png`});
    Object.assign(observations,{played:true,actions:['start','pause','select road','place roads','select construction tent',
      'place tent','run at 8x','pause','open almanac'],roads_built:roads,tents_built:tents,initial,final});
  } else throw new Error(`Unknown game ${repo}`);
  writeFileSync('docs/gameplay.json',JSON.stringify(observations,null,2)+'\n');
  console.log(JSON.stringify(observations));
} finally { await browser.close(); }
