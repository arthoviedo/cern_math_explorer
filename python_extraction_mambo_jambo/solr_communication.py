# -*- coding: utf-8 -*-
## This file is part of Invenio.
## Copyright (C) 2013 CERN.
##
## Invenio is free software; you can redistribute it and/or
## modify it under the terms of the GNU General Public License as
## published by the Free Software Foundation; either version 2 of the
## License, or (at your option) any later version.
##
## Invenio is distributed in the hope that it will be useful, but
## WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
## General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with Invenio; if not, write to the Free Software Foundation, Inc.,
## 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.

import solr
import string
from math_config import *

conn = solr.Solr('http://localhost:8983/solr')
seqid = 0

def add_math_file(recid, file_name, file_path):
    """
    Adds one new document to the solr server.
    The seqid is one sequential id used in solr to identify document
    recid is the record id used in invenio
    filename is the filename that contains the equations 
    """
    
    global seqid
    print "adding doc #" + str(recid) + ":" + str(file_name)
    
    added_file = open(file_path)
    #Get all different equations in the document and stores the number of occurences
    different_equations = {}
    for line in added_file:
        if len(line) < MINIMUM_ACCEPTED_LENGTH:
            continue
        if line in different_equations:
            different_equations[line] += 1
        else:
            different_equations[line] = 1
    docs = []
    for equation_frequencies in different_equations.items(): 
        line = equation_frequencies[0]
        frequency = equation_frequencies[1]
        #print line
        #To be removed, only for testing
        file_name = string.replace(file_name, ".eq", "").replace("arx", "")
        
        doc = dict(id=str(seqid), 
                   title=recid, 
                   math_notational_field=line,      #Index notational features 
                   math_structural_field=line,      #Index structural features
                   number_occurences = frequency, 
                   filename=file_name)
        seqid += 1
        docs += [doc]
    conn.add_many(docs)
    #print doc
    conn.commit()


def query_math_docs(query_string, query_format):
    select = solr.SearchHandler(conn, "/select")
    request_string = "{!mathqueryparser} FORMAT(" + query_format.lower() + ")" + query_string+ "&rows=20"
    print request_string
    response = select.__call__(request_string)
    if not response:
        print "No results"
    else:
        for hit in response:
            print hit['filename'] + ":" + hit['math_notational_field']
    return response

#add_math_file(1, "arx1312.6708.eq", "/share/math/arxiv_cds/arx1312.6708.eq")