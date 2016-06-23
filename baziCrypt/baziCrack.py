import sys

f = open(sys.argv[1], 'r')
c = f.read().encode("hex")
k = sys.argv[2]
mult = int(len(c)/len(k))

for i in range(mult+1):
	k += k

nk = k[:len(c)]
print len(c)
print len(nk)

hx =  hex(int(c, 16) ^ int(nk, 16))
print "Length: " + str(len(hx[2:-1])) + "\n"
print hx
print "\n\n Ascii:\n"
print hx[2:-1].decode("hex")
