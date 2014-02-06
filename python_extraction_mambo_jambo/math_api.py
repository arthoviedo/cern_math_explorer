# -*- coding: utf-8 -*-

# # This file is part of Invenio.
# # Copyright (C) 2010, 2011 CERN.
# #
# # Invenio is free software; you can redistribute it and/or
# # modify it under the terms of the GNU General Public License as
# # published by the Free Software Foundation; either version 2 of the
# # License, or (at your option) any later version.
# #
# # Invenio is distributed in the hope that it will be useful, but
# # WITHOUT ANY WARRANTY; without even the implied warranty of
# # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# # General Public License for more details.
# #
# # You should have received arxiv_id copy of the GNU General Public License
# # along with Invenio; if not, write to the Free Software Foundation, Inc.,
# # 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
import sys
import re
import ntpath
import os
import codecs
import urllib2
import gzip
import tarfile
import shutil
import unicodedata
import time
from math_config import *
from multithreading_utils import ThreadPool
#from invenio import shellutils
from xml.dom.minidom import parseString 
#from invenio.shellutils import run_process_with_timeout
from solr_communication import *

"""
This module is in charge of extracting the MathML tags from arxiv_id given LaTeX file.
"""

FILE_NOT_FOUND = "File not found"

def clean_equation(mathml_string):
    """
    Removes information in the mathml expresion that is not relevant 
    for the indexing and searching process:
    This information contains display features like color, size
    or style of the text, ids, and other additional metadata.
    """
    math_flags = re.UNICODE
    mathml_string = re.sub("\n", "", mathml_string, flags = math_flags)
    mathml_string = re.sub(" align=\".*?\"", "", mathml_string, flags = math_flags)
    mathml_string = re.sub(" class=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" columnalign=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" columnspacing=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" columnalign=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" display=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" displaystyle=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" fence=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" height=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" id=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" lspace=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" mathbackground=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" mathcolor=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" mathsize=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" mathvariant=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" movablelimits=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" scriptlevel=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" separator=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" stretchy=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" style=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" rowalign=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" rowspacing=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" rspace=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" xmlns=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" xml:id=\".*?\"", "", mathml_string, math_flags)
    mathml_string = re.sub(" width=\".*?\"", "", mathml_string, math_flags)
    
    mathml_string = re.sub("<annotation.*?</annotation>", "", mathml_string, math_flags)
    mathml_string = re.sub("<mpadded>|</mpadded>", "", mathml_string, math_flags)
    mathml_string = re.sub("<mstyle>|</mstyle>", "", mathml_string, math_flags)
    mathml_string = re.sub("<semantics>|</semantics>", "", mathml_string, math_flags)
    mathml_string = re.sub("<span>|</span>", "", mathml_string, math_flags)
    mathml_string = re.sub("<p>|</p>", "", mathml_string, math_flags)
    
    #Removes leading and trailing spaces (also thin spaces, 2,3,4 em per space ...)
    mathml_string = re.sub(u"(<mn>)(.*?)(</mn>)", remove_spaces, mathml_string, math_flags)
    mathml_string = re.sub(u"(<mo>)(.*?)(</mo>)", remove_spaces, mathml_string, math_flags)
    mathml_string = re.sub(u"(<mi>)(.*?)(</mi>)", remove_spaces, mathml_string, math_flags)
    
    return mathml_string


def remove_spaces (matchobj):
    """
    Remove all different space caracters that appear in the expression
    """
    result = ""
    u = unicode(matchobj.group(0))
    for c in u:
        if not unicodedata.category(c) == 'Zs':
            #if not re.regex.match(u"[[:space:]]", u):
            result += c
    return result


def clean_temp_folder():
    """
    Removes temp files created in the temp folder
    """
    shutil.rmtree(CONFIG_TEMP_LOCATION)
    os.mkdir(CONFIG_TEMP_LOCATION)

def download_temp_file(url, filename):
    """
    Returns the filename of the downloaded file or "FILE_NOT_FOUND"
    if the file could not be found
    """
    try:
        response = urllib2.urlopen(url)
        temp_file = open(CONFIG_TEMP_LOCATION + filename, 'w')
        temp_file.write(response.read())
        temp_file.close()
        return CONFIG_TEMP_LOCATION + filename, 'w'
    except Exception:
        return FILE_NOT_FOUND


def equations_from_arxiv(arxiv_id):
    """
    Given an arxiv submission id, downloads the source code of the submission,
    and tries to process the file to find the LaTeX files.
    For each of the founded LaTeX files, tries to run the lateXML command and 
    tries to extract the mathml tags in these files.  
    """
    temp_file = download_temp_file(CONFIG_ARXIV_SOURCE_URL + arxiv_id, arxiv_id)
    if temp_file == FILE_NOT_FOUND:
        return []
    tex_files_list = []
    equations_in_submission = []
    expand_tex_files(CONFIG_TEMP_LOCATION + arxiv_id, tex_files_list)
    for tex_file in tex_files_list:
        try:
            xhtml_string = latex_file_to_xhtml(tex_file)
            equations_in_submission += equations_from_xhtml(xhtml_string)
        except Exception as e:  #The file could not be processed, but the 
                                #process can continue
            pass
    return equations_in_submission


def equations_from_xhtml(xhtml_string):
    """ 
    Return arxiv_id list of the math elements in the xhtml string
    """
    try:
        xhtml_string = re.sub("\n:", "", xhtml_string)
        xhtml_string = re.sub("m:", "", xhtml_string)
        dom = parseString(xhtml_string)
        response = []
        for node in dom.getElementsByTagName('math'):
            mathml_expression = node.toxml()
            mathml_expression = clean_equation(mathml_expression) 
            response += [mathml_expression]
        return response
    except Exception as e:
        print e
        return ""


def expand_tex_files(filename, accumulator):
    """
    Given arxiv_id filename tries to explore recursively to find all the possible
    TeX subfiles: If it is arxiv_id directory navigates in it. If it is arxiv_id compressed file, tries to 
    uncompress it.
    This method receives an accumulator list of the TeX files found so far
    """
    filetype = get_file_type(filename)
    if "LaTeX" in filetype:
        accumulator+= [filename]
    elif "directory" in filetype:
        for current_file in os.listdir(filename):
            expand_tex_files(filename+"/"+current_file, accumulator)
    elif "gzip compressed data, was" in filetype: #Single compressed file .gz
        f = gzip.open(filename)
        content = f.read()
        temp_file_name = CONFIG_TEMP_LOCATION+"_"+filename_from_path(filename)
        temp_file = open(temp_file_name, 'w+')
        temp_file.write(content)
        temp_file.close()
        f.close()
        expand_tex_files(CONFIG_TEMP_LOCATION+"_"+filename_from_path(filename), accumulator)
        os.remove(filename)
        pass
    elif "gzip compressed data, from Unix" in filetype or\
        "POSIX tar archive" in filetype: #tar.gz file
        tar = tarfile.open(filename)
        for member in tar.getmembers(): #Uncompress all the files in the tar.gz
            temp_file_name = CONFIG_TEMP_LOCATION+"_"+member.name
            #If arxiv_id file is compressed, it doesnt always 
            #respect the standard tree structure
            basedir = os.path.dirname(temp_file_name)
            if not os.path.exists(basedir):
                os.makedirs(basedir)
            if member.isdir():
                if not os.path.exists(CONFIG_TEMP_LOCATION+"_"+member.name):
                    os.makedirs(CONFIG_TEMP_LOCATION+"_"+member.name)
                continue
            content_file = tar.extractfile(member).read()
            temp_file = open(temp_file_name, 'w+')
            temp_file.write(content_file)
            temp_file.close()
            expand_tex_files(temp_file_name, accumulator) 
        tar.close()
        os.remove(filename)
    else:
        os.remove(filename)

def filename_from_path(path):
    """ 
    Returns the file name given the path of arxiv_id file
    It takes care of the case where the file ends with arxiv_id /
    Taken from: http://stackoverflow.com/questions/8384737/python-extract-file-name-from-path-no-matter-what-the-os-path-format
    """
    head, tail = ntpath.split(path)
    return tail or ntpath.basename(head)


def get_file_type(filename):
    """
    Returns arxiv_id description of the tipe of file that was found
    """
    return shellutils.run_shell_command(cmd="file %s", args=(filename,))[1]


def is_empty_file(path):
    """
    Returns whether the file in the given path is empty or not
    """
    return os.stat(path).st_size == 0


def latex_file_to_xhtml(latex_file_path):
    """ 
    Runs latexml and latexmlpost that converts the latex input file
    into its xhtml representation
    If the latexml process couldnt process the file, the function return an empty string
    @param latex_file_path: (string) path to the latex file
    @return: The xhtml response of invoking latexml command on the input file.
    """
    if is_empty_file(latex_file_path):
        return ""
    filename = filename_from_path(latex_file_path)
    
    cmd_first_step = ["latexml" ,
                latex_file_path,
                "--nocomments",
                "--dest=" + CONFIG_TEMP_LOCATION + filename + ".xml",
                "--quiet"]
    cmd_second_step = ["latexmlpost" ,
                CONFIG_TEMP_LOCATION + filename + ".xml",
                "--format=xhtml",
                "--dest=" + CONFIG_TEMP_LOCATION + filename + ".xhtml"]
    try:
        run_process_with_timeout(cmd_first_step, timeout=CONFIG_LATEXML_TIMEOUT)
        run_process_with_timeout(cmd_second_step, timeout=CONFIG_LATEXML_TIMEOUT)
    except Exception as e:
        print "Error converting the latex file to mathml: " + str(e)
        raise
    if not os.path.exists(CONFIG_TEMP_LOCATION + filename + ".xhtml"):
        return ""
    response = codecs.open(CONFIG_TEMP_LOCATION + filename + ".xhtml", encoding='utf-8')\
        .read().encode('utf-8')
    return response


def process_latex_file(input_file, output_file):
    try:
        temp_file = open(output_file, 'w')
        equations_in_file = \
        equations_from_xhtml(latex_file_to_xhtml(input_file))
        for eq in equations_in_file:
            eq = re.sub("\n", "", eq)
            temp_file.write(eq.encode('utf-8')+"\n")
    except Exception as e:
        print "Error while processing file: " + input_file +": " + str(e)
        raise
    temp_file.close()


def process_directory(input_dir, output_dir):
    """
    For each of the latex files in the folder, runs the extraction process and
    stores the result in the output dir
    """
    succesful_files_converted = 0 
    list_tex_files = [] 
    expand_tex_files(input_dir, list_tex_files)
    pool = ThreadPool(10)
    for tex_file in list_tex_files:
        try:
            filename = filename_from_path(tex_file)
            #Skips already processed files
            #Useful for incremental runs
            if os.path.isfile(output_dir + filename+".eq"):
                continue
            pool.add_task(process_latex_file, tex_file, \
                          output_dir + filename+".eq", timeout=300)
            succesful_files_converted += 1
        except Exception as e:
            print "Error while processing file " + filename +": " + str(e)
            pass
    pool.wait_completion()
    print "Succesful converted files: " + \
    str(succesful_files_converted)+"/"+str(len(os.listdir(input_dir)))


count = 0
def process_arxiv_id(arxiv_id):
    """
    Process the arxiv id and stores the result in 
    dataset location 
    """
    global count
    equations_in_file = equations_from_arxiv(arxiv_id)
    temp_file = open(CONFIG_DATASET_LOCATION+"arx"+arxiv_id+".eq", 'w')
    for eq in equations_in_file:
         eq = re.sub("\n", "", eq)
         temp_file.write(eq.encode('utf-8')+"\n")
    temp_file.close()
    count += 1
    #Hack to balance between cleaning the temp folder each time 
    #and doing it at the end.
    print "Processed: " + arxiv_id

"""
Testing code
"""

def index_in_solr():
    """
    For each metadocument in the dataset location, scans it and index each formula in solr
    """
    
    "Read the mapping from arxiv_ids to record ids"
    f = open("./recids_with_arxiv_ids_for_arthur")
    arxiv_id_to_recid = {}
    for line in f.readlines():
        rec_id = str.split(line)[0]
        arxiv_id = str.split(line)[1]
        arxiv_id = str.split(arxiv_id, ":")[1]
        arxiv_id_to_recid[arxiv_id] = rec_id
            
    docid = 100000
    list_docs = os.listdir(CONFIG_DATASET_LOCATION)
    lastDoc = 0
    for i in range(len(list_docs)):
        docid+=1
        if(i >= lastDoc):
            to_add_filename = list_docs[i]
            #print to_add_filename
            arxiv_id = str.replace(to_add_filename, "arx", "").replace(".eq", "")
            add_math_file(arxiv_id_to_recid[arxiv_id], 
                          to_add_filename, CONFIG_DATASET_LOCATION + to_add_filename)
            print str(docid) +"   " +  str(time.strftime("%c"))

def process_dataset():
    f = open("./recids_with_arxiv_ids_for_arthur")
    pool = ThreadPool(5)
    for line in f.readlines():
        try:
            arxiv_id = str.split(line)[1]
            arxiv_id = str.split(arxiv_id, ":")[1]
            if not os.path.isfile(CONFIG_DATASET_LOCATION+"arx"+arxiv_id+".eq"):
                pool.add_task(process_arxiv_id,arxiv_id, timeout=400)
        except Exception as e:
            print "Error while processing id: " + arxiv_id
            raise e
    pool.wait_completion()

index_in_solr()
#process_arxiv_ids(arxiv_ids_test)
#process_dataset()