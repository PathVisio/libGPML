---
title: 'libGPML: a Java API Library for reading, writing, and conversion of GPML'
tags:
  - bioinformatics
  - pathvisio
  - GPML
  - WikiPathways 
authors:
  - name: Finterly Hu
    orcid: 0000-0002-8416-4533
    affiliation: 1
  - name: Martina Kutmon
    orcid: 0000-0002-7699-8191
    affiliation: 1 
  - name: 
    orcid:
    affiliation: 1 
affiliations:
 - name: Dept of Bioinformatics - BiGCaT, NUTRIM, Maastricht University
   index: 1
date: 25 June 2022
bibliography: paper.bib
---

# Summary
LibGPML is an open-source application programming interface (API) library for reading, writing and manipulating pathway models in the Graphical Pathway Markup Language (GPML) format.  It is written in Java and runs on all major operating systems.  LibGPML not only provides convenience for developers who need to parse GPML in their applications, but also includes many features that facilitate use of GPML and the library itself, including pathway validation and conversion between GPML2013a and the latest released version GPML2021.

# Statement of Need
Pathways provide a powerful visual representation of biological concepts for data exploration. 
The study and collection of biological pathways is valuable for research such as insights into complicated disease mechanisms, computational analysis and interpretation of large-scale experimental data. 

Graphical Pathway Markup Language (GPML), an open XML-based format, was developed to effectively represent pathway models and their elements with biological information. GPML is the file format used to store and share pathway content at WikiPathways [@Kelder:2012], a community curated pathway database, and is compatible with pathway visualization and analysis software tools such as Cytoscape [@Shannon:2003] and Pathvisio [@Kutmon:2012], the latter of which uses GPML as its native format. 

LibGPML was developed specially for the parsing of pathway models in the GPML format. Although there are many off-the-shelf-XML parser libraries, a higher-level API library such as libGPML tailored specifically to GPML provides ease of use and added capabilities. LibGPML features such as validation of models help improve the consistency and quality of created pathway models.  LibGPML also seamlessly reads, writes, and converts GPML2013a and the latest GPML2021 format, which introduced new attributes and type definitions, and extended capability to annotate, cite literature, and add evidence to pathway elements. Software tools such as LibGPML, which work with pathway data, can help researchers to better understand, share and discuss knowledge. LibGPML will be embedded in the latest version of PathVisio 4.0.0 and its plugins. 

# Implementation


## Continuous integration and releases
LibGPML is hosted on GitHub.


# Figures

![libGPML UML Class Diagram.\label{fig:example}](https://github.com/PathVisio/libGPML/blob/main/.graphics/libgpml_diagram.svg){ width=90% }

and referenced from text using \autoref{fig:example}.



# Use cases
Mention (if applicable) a representative set of past or ongoing research projects using the software and recent scholarly publications enabled by it.

Pathvisio and GPML scripts have been in use in our group in various research lines to automate repetitive work.


# Acknowledgements
Acknowledgement of any financial support.

We acknowledge the contributions 

# References
A list of key references, including to other software addressing related needs. Note that the references should include full names of venues, e.g., journals and conferences, not abbreviations only understood in the context of a specific discipline.

