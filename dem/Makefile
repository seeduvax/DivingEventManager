
CYGWIN=$(shell test -x /usr/bin/cygpath && echo true || echo false)
ifeq ($(CYGWIN),true)
	PATH_SEPARATOR=;
	LN=cp
else
	PATH_SEPARATOR=:
	LN=ln -sf
endif	

MODE=Debug
FORCECONFIG=$(shell build/jco.sh $(MODE) $(DEFINES))
	
HASJARPATH=$(shell test -d "$(JARPATH)" && echo true ||echo false)
ifeq ($(HASJARPATH),false)
	JARPATH=$(shell cd ../jars ; pwd)
endif

HTTPJARS=http://www.eduvax.net/jdev/jars

PACKAGE=net.eduvax.dem
VERSION=$(shell cat .egpi-brspec)
REVISION=$(shell svnversion | sed 's/:/./')
MAINCLASS=net.eduvax.dem.DEM
SHORTNAME=$(patsubst net.eduvax.%,%,$(PACKAGE))

LICENSE=lgpl-2.0
LICENSEFILE=build/lgpl-2.0.txt
		

lib/%.jar: $(JARPATH)/%.jar
	$(LN) $< $@

$(JARPATH)/%.jar:	
	wget $(patsubst $(JARPATH)/%.jar,$(HTTPJARS)/%.jar,$@) -O $@
	
all: bin/$(PACKAGE).jar
	make all.local

all.local::

.PHONY: doc

JAR=jar
JAVA=java
JAVAC=javac
JAVADOC=javadoc -private
CLASSP=src/res
RUNCP=bin/$(PACKAGE).jar

LICENSE=lgpl-2.0

LICENSEFILE=build/$(LICENSE).txt
ifeq ($(JARPATH),lib)
DISTLIB=
LIBS=
else
DISTLIB=$(LIBS) lib/junit.jar lib/net.eduvax.test.jar
LIBS=
endif
JFLAGS=-classpath "${CLASSP}" -d bin -sourcepath "src$(PATH_SEPARATOR)obj" 
ifeq ($(MODE),Release)
	JFLAGS+= -g:none
else
	JFLAGS+= -g
endif
CLASSFILES=$(patsubst src/%.java,bin/%.class,$(shell find src -name '*.java' | grep -v "^src/test/"))
TESTCLASSP=lib/junit.jar$(PATH_SEPARATOR)lib/net.eduvax.test.jar$(PATH_SEPARATOR)tbin$(PATH_SEPARATOR)$(CLASSP)
TESTCLASSFILES=$(patsubst src/test/%.java,tbin/test/%.class,$(shell find src/test -name 'Test*.java'))
TESTSUITE=$(patsubst tbin/test/%.class,test.%,$(TESTCLASSFILES))
RESFILES=$(patsubst src/%,bin/%,$(shell find src -name '*.html' -o -name '*.png' -o -name '*.properties' | grep -v "^src/test/" | grep -v "^src/doc"))
HTMLFILES=$(patsubst src/doc/%.heml,doc/%.html,$(shell find src/doc -name '*.heml')) $(patsubst src/doc/%,doc/%,$(shell find src/doc -name '*.png' -o -name '*.css'))

HASLOCAL=$(shell test -f src/Makefile && echo true || echo false)
ifeq ($(HASLOCAL),true)
include src/Makefile
endif

setup: $(LIBS) .project .classpath 

header:
	build/addHeader.sh $(LICENSE)

config:	
	build/jco.sh $(MODE) $(DEFINES)

bin/%.class: src/%.java .init.dirs $(LIBS) $(FORCECONFIG)
	${JAVAC} ${JFLAGS} $< 

bin/%.html: src/%.html	
	cp $< $@

bin/%.png: src/%.png	
	cp $< $@

bin/%.properties: src/%.properties	
	cp $< $@

doc/%.html: src/doc/%.heml
	cat $< | heml | xsltproc --stringparam buildDate "`date -R`" build/style.xhtml.xsl - > $@

doc/%: src/doc/%
	cp $< $@

tbin/test/%.class: src/test/%.java .init.dirs bin/$(PACKAGE).jar $(LIBS) lib/junit.jar lib/net.eduvax.test.jar
	${JAVAC} -classpath "${TESTCLASSP}" -d tbin -sourcepath src$(PATH_SEPARATOR)obj $<

%: tbin/test/%.class
	$(JAVA) -cp "$(TESTCLASSP)" net.eduvax.test.TestRunner test.$@
	


Makefile: build/jmakefile.xsl src/package.xml build/setup.sh
	xsltproc build/jmakefile.xsl src/package.xml > Makefile 

.classpath: build/classpath.xsl src/package.xml build/setup.sh
	xsltproc build/classpath.xsl src/package.xml > .classpath 

.project: build/project.xsl src/package.xml build/setup.sh
	xsltproc --param version `cat .egpi-brspec | sed 's/\.[0-9]*$$//g'` build/project.xsl src/package.xml > .project
	
.init.dirs: Makefile
	mkdir -p bin dist lib tbin obj doc ; \
	date > .init.dirs ; \
	make .init.dirs.local 
	
.init.dirs.local::

clean: dist.clean
	rm -rf tbin bin obj *.jar .init.dirs $(PACKAGE)*.tar.gz doc/* ; \
	make clean.local

clean.local::	

clean.doc:
	rm -rf doc ; \
	make clean.doc.local

clean.doc.local::	

clean.all: clean clean.doc clean.dist

bin/$(PACKAGE).jar: $(CLASSFILES) $(RESFILES)
	cd bin && \
	rm -rf $(PACKAGE).jar ; \
	echo "Main-Class: "$(MAINCLASS) > ../Manifest ; \
	echo "Class-Path: "$(patsubst lib/%.jar,../lib/%.jar,$(LIBS))" ../res" >> ../Manifest ; \
	echo "Implementation-Version: "$(VERSION)-$(REVISION) >> ../Manifest ; \
	echo "Implementation-Vendor: Sebastien Devaux" >> ../Manifest ; \
	echo "Implementation-Vendor-Id: eduvax.net" >> ../Manifest ; \
	echo "Implementation-URL: "http://www.eduvax.net/jdev/$(PACKAGE) >> ../Manifest ; \
	echo "Implementation-Licence: "$(LICENSE) >> ../Manifest ; \
	echo "Build-Date: "`date` >> ../Manifest ; \
	echo "Build-Host: "`hostname` >> ../Manifest ; \
	echo "Build-Option: "$(MODE) $(DEFINES) >> ../Manifest ; \
	rm -rf jc ; \
	$(JAR) cvf $(PACKAGE).jar * && \
	jar uvmf ../Manifest $(PACKAGE).jar ; \
	cd .. ; \
	rm Manifest

install: bin/$(PACKAGE).jar
	mkdir -p ${JARPATH} ; \
	cp bin/$(PACKAGE).jar ${JARPATH}/$(PACKAGE).$(VERSION)-$(REVISION).jar ; \
	make install.local
	
install.local::

run: bin/$(PACKAGE).jar
	$(JAVA) -jar bin/$(PACKAGE).jar $(RUNARGS) ; \
	make run.local

run.local::	

doc: .init.dirs $(HTMLFILES)
	$(JAVADOC) -d doc/api -classpath "$(CLASSP)" -sourcepath src -subpackages $(PACKAGE) ; \
	make doc.local

doc.local::	
	
test: $(TESTCLASSFILES)
	$(JAVA) -cp "$(TESTCLASSP)" net.eduvax.test.TestRunner $(TESTSUITE) ; \
	make test.local
	
test.local::

dist/$(PACKAGE)-src.$(VERSION)-$(REVISION).tar.gz: Makefile $(DISTLIB)
	mkdir -p $(PACKAGE)-src.$(VERSION)-$(REVISION) ; \
	cp $(LICENSEFILE) $(PACKAGE)-src.$(VERSION)-$(REVISION)/COPYING ; \
	ln -s ../src $(PACKAGE)-src.$(VERSION)-$(REVISION)/src ; \
	ln -s ../lib $(PACKAGE)-src.$(VERSION)-$(REVISION)/lib ; \
	ln -s ../Makefile $(PACKAGE)-src.$(VERSION)-$(REVISION)/Makefile ; \
	ln -s ../build $(PACKAGE)-src.$(VERSION)-$(REVISION)/build ; \
	ln -s ../.egpi-brspec $(PACKAGE)-src.$(VERSION)-$(REVISION)/.egpi-brspec ; \
	tar cvzhf dist/$(PACKAGE)-src.$(VERSION)-$(REVISION).tar.gz \
		--exclude .svn --exclude CVS $(PACKAGE)-src.$(VERSION)-$(REVISION) ; \
	rm -rf $(PACKAGE)-src.$(VERSION)-$(REVISION)	

dist.src: dist/$(PACKAGE)-src.$(VERSION)-$(REVISION).tar.gz		
	make dist.local

clean.dist:
	rm -rf $(PACKAGE)*.tar.gz

dist.local::

dist.bin: bin/$(PACKAGE).jar
	mkdir $(PACKAGE).$(VERSION)-$(REVISION) $(PACKAGE).$(VERSION)-$(REVISION)/bin $(PACKAGE).$(VERSION)-$(REVISION)/lib ; \
	cp bin/$(PACKAGE).jar $(PACKAGE).$(VERSION)-$(REVISION)/bin ; \
	cp $(LIBS) $(PACKAGE).$(VERSION)-$(REVISION)/lib ; \
	echo "#!/bin/sh" > $(PACKAGE).$(VERSION)-$(REVISION)/bin/$(SHORTNAME).sh ; \
	echo 'exec java -jar `dirname $$0`/'"$(PACKAGE).jar"' "$$@"' >> $(PACKAGE).$(VERSION)-$(REVISION)/bin/$(SHORTNAME).sh ; \
	chmod a+x $(PACKAGE).$(VERSION)-$(REVISION)/bin/$(SHORTNAME).sh ; \
	echo "java -jar $(PACKAGE).jar" > $(PACKAGE).$(VERSION)-$(REVISION)/bin/$(SHORTNAME).bat ; \
	test -d src/res && cp -r src/res $(PACKAGE).$(VERSION)-$(REVISION)/res ; \
	find $(PACKAGE).$(VERSION)-$(REVISION)/res -name ".svn" | xargs rm -rf ; \
	cp $(LICENSEFILE) $(PACKAGE).$(VERSION)-$(REVISION)/COPYING ; \
	tar cvzf dist/$(PACKAGE).$(VERSION)-$(REVISION).tar.gz $(PACKAGE).$(VERSION)-$(REVISION) ; \
	rm -rf $(PACKAGE).$(VERSION)-$(REVISION) ; \
	make dist.bin.local

dist.eclipse: bin/$(PACKAGE).jar
	cp bin/$(PACKAGE).jar dist/$(PACKAGE).$(VERSION).jar ; \
	cd src ; \
	$(JAR) uvf ../dist/$(PACKAGE).$(VERSION).jar  `find . -name '*.java' | grep -v "^\./test/"` ; \
	cd .. ; \
	cp $(LICENSEFILE) COPYING ; \
	$(JAR) uvf dist/$(PACKAGE).$(VERSION).jar COPYING ; \
	rm COPYING

dist.clean:
	rm -rf dist/$(PACKAGE).$(VERSION).jar dist/$(PACKAGE).$(VERSION)-$(REVISION).tar.gz dist/$(PACKAGE)-src.$(VERSION)-$(REVISION).tar.gz

dist.bin.local::	

dist.all: dist.src dist.bin dist.eclipse

check.dist: dist.src
	tar -xvzf dist/$(PACKAGE)-src.$(VERSION).tar.gz ; \
	cd $(PACKAGE)-src.$(VERSION)-$(REVISION) ; \
	make JARPATH=lib test ; \
	make check.dist.local ; \
	cd .. ; \
	rm -rf $(PACKAGE)-src.$(VERSION)-$(REVISION)

check.dist.local::	
	
