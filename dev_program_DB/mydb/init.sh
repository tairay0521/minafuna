#!/bin/bash
# Copyright(C) 2007 National Institute of Informatics, All rights reserved.
java -cp ../lib/hsqldb.jar org.hsqldb.util.SqlTool \
  --autoCommit \
  --inlineRc "url=jdbc:hsqldb:hsql://localhost,user=SA,password=''" \
  setup.sql