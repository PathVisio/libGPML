---
title: 'libGPML: a Java Library for reading, writing, and converting GPML'
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
date: 25 June 2021
bibliography: paper.bib
---



# Summary
describing the high-level functionality and purpose of the software for a diverse, non-specialist audience.

GPML (Graphical Pathway Markup Language) was originally... It was later ....libGPML is a Java library....that is more easily updated, built, released, and used. A subset of the original functionality is available, and some managers have already been updated to
use more recent versions of dependencies.





# Statement of Need
clearly illustrates the research purpose of the software.

Moving to GPML2021 format....While pathvisio core library has served our research for many years, a number of limitations has made
this increasingly hard. ...

While Bioclipse has served our research for many years, a number of limitations has made this increasingly hard. For example, the dependency of Bioclipse on the Eclipse UI requires the scripts to be run inside a running Bioclipse application. This makes repeatedly running of a script needlessly hard and use in continuous integration systems or use on computing platforms impossible. A second problem was that the build and release system of Bioclipse was complex, making it hard for others to repeat creating new releases. This is reflected in the lack of recent releases and complicates the process for external developers wishing to make patches.

These needs triggered a next generation design of Bioclipse: 1. the managers providing the domain-specific functionality would need to be usable on the command line; 2. building the Bioclipse managers should be possible on the command line, ideally with continuous build systems; 3. Bacting should be easy to install and reuse.

# Implementation
## Continuous integration and releases





# Use cases
Mention (if applicable) a representative set of past or ongoing research projects using the software and recent scholarly publications enabled by it.
Pathvisio and GPML scripts have been in use in our group in various research lines to automate repetitive work.


# Figures

Figures can be included like this:

  <img width="100%" src=".graphics/libgpml_diagram.svg">

![libGPML UML diagram.\label{fig:example}](https://github.com/PathVisio/libGPML/blob/main/.graphics/libgpml_diagram.svg)
and referenced from text using \autoref{fig:example}.

![Caption for example figure.\label{fig:example}](figure.png)
and referenced from text using \autoref{fig:example}.

Figure sizes can be customized by adding an optional second parameter:
![Caption for example figure.](figure.png){ width=20% }


# Acknowledgements
Acknowledgement of any financial support.

We acknowledge the contributions 

# References
A list of key references, including to other software addressing related needs. Note that the references should include full names of venues, e.g., journals and conferences, not abbreviations only understood in the context of a specific discipline.

