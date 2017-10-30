DEST=/usr/local

all: clean
	astyle -q -A2 it/ciano/cnxrename/*.java
	javac -encoding UTF-8 it/ciano/cnxrename/*java
	jar cfm cnxrename.jar Manifest.txt it/ciano/cnxrename/*class
	rm -f it/ciano/cnxrename/*orig it/ciano/cnxrename/*class

install: all
	if [[ ! -d ${DEST}/share/cnxrename ]] ; then mkdir ${DEST}/share/cnxrename ; fi
	install -m 644 LICENSE README.md cnxrename.jar ${DEST}/share/cnxrename
	echo '#!/usr/bin/perl' > ${DEST}/bin/cnxrename
	echo 'system( "/usr/bin/java", "-jar", "${DEST}/share/cnxrename/cnxrename.jar", @ARGV );' >> ${DEST}/bin/cnxrename
	chmod 755 ${DEST}/bin/cnxrename

python:
	rm -fr ${DEST}/bin/cnxrename
	install -m 755 p3cnxrename.py ${DEST}/bin/cnxrename

uninstall:
	if [[ -d ${DEST}/share/cnxrename ]] ; then rm -fr ${DEST}/share/cnxrename ; fi
	if [[ -f ${DEST}/bin/cnxrename ]] ; rm ${DEST}/bin/cnxrename ; fi

clean:
	rm -f it/ciano/cnxrename/*orig it/ciano/cnxrename/*class cnxrename.jar
