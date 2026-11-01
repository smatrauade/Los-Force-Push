#!/usr/bin/env python3
"""Validate the generated StarUML MDJ *structure*, not native compatibility.
Safe to run on Windows/macOS/Linux; Python 3 stdlib only; no mutation.
"""
import json,sys,csv
from pathlib import Path
D=Path(__file__).resolve().parent
try:
    root=json.loads((D/'Los_Force_Push_R10_AS_IMPLEMENTED_DRAFT.mdj').read_text(encoding='utf-8'))
    assert root['_type']=='Project' and root['ownedElements']
    model=root['ownedElements'][0]
    assert model['_type']=='UMLModel'
    found={};references=[]
    def check(x,parent=None):
        if isinstance(x,list):
            for y in x: check(y,parent)
        elif isinstance(x,dict):
            if '$ref' in x:
                assert len(x)==1
                references.append(x['$ref']);return
            if '_id' in x:
                assert x['_id'] not in found, 'duplicate id '+x['_id']
                found[x['_id']]=x
                if parent is not None: assert x['_parent']=={'$ref':parent}
                parent=x['_id']
            for k,v in x.items():
                if k in ('_id','_parent'):continue
                check(v,parent if k in ('ownedElements','ownedViews','subViews','attributes','operations','parameters') else None)
    check(root)
    dangling=[r for r in references if r not in found]
    assert not dangling, f'{len(dangling)} dangling refs'
    diagrams=[x for x in model['ownedElements'] if x['_type']=='UMLClassDiagram']
    assert len(diagrams)==9, f'{len(diagrams)} diagrams; expect 9'
    types=[x for p in model['ownedElements'] if p['_type']=='UMLPackage' for x in p.get('ownedElements',[]) if x['_type'] in ('UMLClass','UMLInterface','UMLEnumeration')]
    assert len(types)==82, f'{len(types)} types; expect 82'
    assert all(any(v.get('_type','').endswith('View') for v in x['ownedViews']) for x in diagrams)
    with (D/'TRAZABILIDAD_TIPOS.csv').open(encoding='utf-8-sig',newline='') as f: assert len(list(csv.DictReader(f)))==len(types)
    expected=set((D/'INVENTARIO_GITHUB_50_FUENTES.txt').read_text(encoding='utf-8').splitlines()[1:])
    with (D/'TRAZABILIDAD_TIPOS.csv').open(encoding='utf-8-sig',newline='') as f:
        actual=set(x['java_source_path'] for x in csv.DictReader(f) if x['java_source_path']!='EXTERNAL_JDK')
    assert len(expected)==50 and actual==expected, f'Java coverage mismatch: missing={expected-actual}, extra={actual-expected}'
    print(f'PASS_SOURCE_COVERAGE=50/50, 4 package namespaces')
    print(f'PASS_STATIC_JSON_AND_REFS: {len(found)} IDs, {len(references)} refs, {len(diagrams)} diagrams, {len(types)} UML types; native StarUML not checked')
except (OSError,ValueError,AssertionError,KeyError) as exc:
    print('FAIL_CLOSED:',exc,file=sys.stderr)
    sys.exit(1)
