CC:=gcc
CFLAGS:=-W -Wall

all: encGen keyGen

encGen: encGen.c
	$(CC) $(CFLAGS) encGen.c -o encGen

keyGen: keyGen.c
	$(CC) $(CFLAGS) keyGen.c -o keyGen

.PHONY: clean
clean:
	rm -f encGen keyGen
