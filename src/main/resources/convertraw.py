#!/usr/bin/python

import sys, re

current = None
lines = []

# merge lines and fixup
for line in sys.stdin:
    if re.search(r'^[A-Z]', line):
        if current is not None:
            lines.append(re.sub(r'[^A-Z0-9,]', '', current))
            current = ""
        current = line
    else:
        current += line

lines.append(re.sub(r'[^A-Z0-9,]', '', current))

data = [ x.split(',') for x in lines ]

max_length = max([ len(x[0]) for x in data ])

print "; max_length", max_length

def extend(l, s):
    while len(s) < l:
        s += "_"
    return s

data2 = [ (extend(max_length, x[0]), x[1]) for x in data ]

print "ProteinSequence,Class"
print "\n".join([",".join(x) for x in data2])
