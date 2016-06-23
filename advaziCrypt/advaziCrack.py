import sys

f = open(sys.argv[1], 'r')
c = f.read().encode("hex")
k = sys.argv[2]
pkc = sys.argv[3]
mult = int(len(c)/len(k))
pk = ""
ks = ""


for j in range(len(k)/2):
	pk += pkc

k = str(hex(int(k, 16) ^ int(pk, 16)))[2:-1]

for i in range(mult+1):
	ks += k

nk = ks[:len(c)]

hx =  hex(int(c, 16) ^ int(nk, 16))
print hx
print "\n\nAscii:\n"
print hx[2:-1].decode("hex")
