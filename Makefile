# ---------------------------------------------------------------------
# ABS bootstrap make file.
# $Rev: 6943 $
# (c) ArianeGroup 2017
#
# To use ABS to build your project, just copy this file as 'Makefile' into
# your project's root directory and each already existing module directories.
# As soon your project's layout is complient to the ABS layout and contains
# the expected configuration files (app.cfg at top level, and module.cfg in 
# each module directory), you can invoke make command to build it.
# ---------------------------------------------------------------------
# Prerequisites: a quite regular shell including GNU make, tar, wget and
# few other widely available commands.
# Any GNU environment (GNU/Linux, cygwin, mingw, may be GNU/hurd) should
# be able to run this makefile.
# ---------------------------------------------------------------------
# See ABS documentation (ref RD_TEA332-1593) for more details.
# https://pforgerle.public.infrapub.fr.st.space.corp/confluence/display/rdtea332/BuildScripts
# ---------------------------------------------------------------------

ifneq ($(wildcard app.cfg),)
PRJROOT:=$(CURDIR)
endif
ifneq ($(wildcard module.cfg),)
PRJROOT:=$(shell dirname $(CURDIR))
endif

include $(PRJROOT)/app.cfg
include $(PRJROOT)/.abs/core/main.mk

ifeq ($(findstring file://,$(ABS_REPO)),file://)
$(PRJROOT)/.abs/%/main.mk:
	@echo Fetching abs $(patsubst $(PRJROOT)/.abs/%/main.mk,%,$@) from $(ABS_REPO)
	@mkdir -p $(PRJROOT)/.abs
	@tar xzf $(patsubst file://%,%,$(patsubst $(PRJROOT)/.abs/%/main.mk,$(ABS_REPO)/noarch/abs.%-$(VABS).tar.gz,$@)) -C $(PRJROOT)/.abs --strip-components=1
	@touch $@

else
$(PRJROOT)/.abs/%/main.mk:
	@echo Fetching abs $(patsubst $(PRJROOT)/.abs/%/main.mk,%,$@) from $(ABS_REPO)
	@mkdir -p $(PRJROOT)/.abs
	@wget -q --no-check-certificate $(patsubst $(PRJROOT)/.abs/%/main.mk,$(ABS_REPO)/noarch/abs.%-$(VABS).tar.gz,$@) -O - | tar xzf - -C $(PRJROOT)/.abs --strip-components=1
	@touch $@

endif

ABS_PRINT:=$(PRJROOT)/.abs/core/abs_print.sh
