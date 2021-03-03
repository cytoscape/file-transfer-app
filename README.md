# file-transfer-app
The FileTransfer app is intended to support workflow execution in 
a Notebook executing on a remote server. Such Notebooks can be
written in Python (via [py4cytoscape](https://github.com/cytoscape/py4cytoscape) ) 
or other languages when/if such support becomes available for them.
To execute a workflow, a remote Notebook must have bi-directional
communication with a workstation-based Cytoscape instance. The 
[Jupyter-Bridge](https://github.com/cytoscape/jupyter-bridge) solves this problem. 

However, Jupyter-Bridge does not address file access problems
caused by remote Notebooks running on a server while Cytoscape runs
on a local workstation. Files written by Cytoscape (e.g., exporting
a .png image) are written to the workstation's file system, and are
not readily accessible by the Notebook. Files maintained on the
Notebook server's file system (e.g., a .cys, .xls or .csv file) 
are not readily accessible by Cytoscape. The File Transfer app 
enables files to be transferred between the remote server and 
local workstation.

It also solves an associated portability and security problem:
a) encoding paths to workstation files/directories in a Notebook 
assumes a uniform directory structure across all Cytoscape 
workstations (which is invariably false), and b) allowing 
a Notebook access to arbitrary workstation paths risks accidental
(or purposeful) corruption of workstation file systems. The File
Transfer app enables file access to be confined to sandboxed 
directories on the workstation.

In all, the File Transfer app enables Notebooks that:
* are portable across Cytoscape workstations
* minimize risk to the workstation file system
* read Cytoscape results
* send Cytoscape data

## Sandboxes
A sandbox is a directory under the workstation's CytoscapeConfiguration/filetransfer
directory, and it contains all files created and consumed by
Cytoscape. The default sandbox is ``default_sandbox``, though
a Notebook can create other sandboxes. The following operations are
defined for sandboxes:

| Function | Use |
| :--- | :--- |
|  setSandbox | defines a new sandbox for subsequent operations  |
|  removeSandbox | deletes a sandbox and its contents  |
|  getFileInfo | gets metadata for a file/directory in a sandbox  |
|  removeFile | removes a file/directory in a sandbox  |
|  toSandbox | copies a file from the Notebook's file system to a sandbox  |
|  fromSandbox | copies a sandbox file to the Notebook's file system |
|  urlToSandbox | copies a file associated with a URL to a sandbox |

While named sandboxes are implemented as subdirectories of the
workstation's CytoscapeConfiguration/filetransfer folder, Notebooks
running on the Cytoscape workstation have the additional option of
a ``null`` sandbox, which enables access to the entire 
workstation file system (at the cost of Notebook portability).

Remotely executing Notebooks are automatically provisioned with 
a ``default_sandbox`` sandbox that contains the data samples
(i.e., the ``dataSamples`` directory) supplied with a
Cytoscape release. To create a Notebook that
can be executed on both the Workstation and on a remote server, a
Notebook can use ``setSandbox`` to explicitly define a sandbox, 
which can optionally contain Cytoscape data samples.

Note that it is not possible for a Notebook to use py4cytoscape
calls and access files outside of a sandbox. 

(Sandboxes are implemented at the py4cytoscape level -- CyREST
calls contain absolute paths calculated by concatenating the workstation's 
absolute sandbox path to whatever file is being accessed or created. This 
approach avoids changes to Cytoscape,
but still enables malicious actors to corrupt the workstation 
file system if they are even slightly clever.)

## FileTransfer Functions
This README does not document individual functions. The best way
to discover these functions and their details is to browse the
``filetransfer`` Swagger pages available off the Cytoscape 
**Help | Automation | CyREST API Commands** menu.

Additionally, the py4cytoscape user documentation contains sections 
on [general Sandbox use](https://py4cytoscape.readthedocs.io/en/latest/concepts.html#sandboxing) and calling [specific Sandbox functions](https://py4cytoscape.readthedocs.io/en/latest/reference/sandbox.html).

## Notes
Note that this app is *not* necessary for py4cytoscape to communicate with Cytoscape, 
but *is* necessary for the Sandbox features needed by Jupyter Notebooks
running on a remote server.

## Rebuilding

Assuming you're developing under Eclipse, you can rebuild the project by performing a Mavan *install* operation:

- In the Package Explorer panel on the top-left, click to expand ``file-transfer`` folder.

- Right-click (or control-click if you're on a Mac) on the ``pom.xml`` file.

- Save your work by using ``File | Save All``

- Select ``Run As``.

- Then select ``Maven install``.

The resulting app will be in the ``file-transfer-xx.jar`` file in the project's main directory (where xx is the version declared in ``version`` attribute of the ``pom.xml`` file). You can test the new app in Cytoscape by copying it into the ``CytoscapeConfiguration/3/apps/installed`` directory. Be sure to first remove any previous app version from that directory.


When Maven finishes building the app, you will see the Build success message in the Console tab, located at the bottom-center of Eclipse.
	
