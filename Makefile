all: clean
	astyle -q -A2 it/ciano/cnxrename/*.java
	javac -encoding UTF-8 it/ciano/cnxrename/*java
	jar cfm cnxrename.jar Manifest.txt it/ciano/cnxrename/*class
	rm -f it/ciano/cnxrename/*orig it/ciano/cnxrename/*class

install: all
	mkdir /usr/local/share/cnxrename
	install -m 644 LICENSE README.md cnxrename.jar /usr/local/share/cnxrename
	echo '#!/usr/bin/perl' > /usr/local/bin/cnxrename
	echo 'system( "/usr/bin/java", "-jar", "/usr/local/share/cnxrename/cnxrename.jar", @ARGV );' >> /usr/local/bin/cnxrename
	chmod 755 /usr/local/bin/cnxrename

clean:
	rm -f it/ciano/cnxrename/*orig it/ciano/cnxrename/*class cnxrename.jar
