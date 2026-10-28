#!/usr/bin/env python3
"""Generador de SFX originales (NO sobreescribe los PNG finales).

Los sprites actuales, ya incluidos en el paquete, son archivos de arte
pixel 32x32. El generador antiguo dibujaba una version descartada y se
eliminó para evitar que regenerar sonidos revierta el rediseno aprobado.
No se requiere este script para ejecutar el juego.
"""
from pathlib import Path
import math
import random
import wave
import struct

ROOT = Path(__file__).resolve().parents[2] / 'src/desktop/resources/assets'
SPR = ROOT / 'sprites'
SFX = ROOT / 'sfx'
SFX.mkdir(parents=True,exist_ok=True)

# Original chiptune SFX (PCM WAV, 22050Hz, 16-bit, mono).
R=22050
def wav(name,segments):
    data=[]
    for duration,freq,freq_end,kind,ampl in segments:
        n=int(R*duration)
        for i in range(n):
            t=i/R;u=i/max(1,n-1); f=freq+(freq_end-freq)*u
            phase=2*math.pi*(freq*t+(freq_end-freq)*t*u/2)
            if kind=='noise':
                rnd=random.Random((i*16777619+len(data))*31)
                val=rnd.uniform(-1,1)
            elif kind=='triangle':val=(2/math.pi)*math.asin(math.sin(phase))
            elif kind=='square':val=1 if math.sin(phase)>=0 else -1
            else:val=math.sin(phase)
            envelope=min(1, i/max(1,R*0.005))*min(1,(n-i)/max(1,n*0.50))
            data.append(int(max(-1,min(1,val*envelope*ampl))*32767))
    with wave.open(str(SFX/(name+'.wav')),'wb') as w:
        w.setnchannels(1);w.setsampwidth(2);w.setframerate(R)
        w.writeframes(struct.pack('<'+'h'*len(data),*data))
S=[(.055,210,135,'triangle',.17)]
wav('step',S)
wav('push',[(.06,115,79,'triangle',.23),(.065,95,54,'noise',.12)])
wav('blocked',[(.09,180,88,'square',.08)])
wav('goal',[(.10,510,590,'triangle',.17),(.13,670,800,'triangle',.16)])
wav('menu',[(.075,560,680,'triangle',.14)])
wav('undo',[(.09,580,360,'triangle',.16),(.07,360,230,'triangle',.12)])
wav('restart',[(.12,325,190,'triangle',.14),(.09,370,440,'triangle',.13)])
wav('fragile',[(.15,180,75,'noise',.20)])
wav('ice',[(.23,670,910,'sine',.13)])
wav('lock',[(.055,490,540,'square',.09),(.14,640,780,'triangle',.15)])
wav('gate',[(.25,155,75,'triangle',.19),(.12,140,90,'noise',.10)])
wav('victory',[(.11,440,500,'triangle',.17),(.12,550,610,'triangle',.17),(.13,660,730,'triangle',.16),(.3,880,920,'triangle',.17)])
print('Sprites finales preservados:',len(list(SPR.glob('*.png'))),'sounds:',len(list(SFX.glob('*.wav'))))
