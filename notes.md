OpenCASCADE is a free/Open Source Geometry Kernel. It's used by FreeCAD, but suffers from:
 - A general complete lack of documentation, inactive community
 - Some robustness and stability issues
 - Interop issues - does its own memory management, GUI toolkits, data representations.
 
 
 Alternatives to it include: 
 - [Parasolid](https://www.plm.automation.siemens.com/global/en/products/plm-components/parasolid.html)
    - closed source, expensive?
    - active community
    - highly robust solid kernel
 - [ACIS](https://www.spatial.com/)
    - alternative to parasolid, but same properties
 - [CGAL](https://doc.cgal.org/latest/Manual/packages.html)
    - open source algorithms library (not a kernel)
    - doesn't include some solid ops like revolve / sweep
    - does include lots of other algorithms on points & meshes
    - widely used and very robust 
 - [FeatureScript](https://cad.onshape.com/FsDoc/)  
    - limited execution environment (browser inside onshape)
    - limited interoperability? can i use it as a library in a synthesizer? 
 - [RhinoCommon](https://developer.rhino3d.com/guides/rhinocommon/)
    - proprietary, nurbs-surface based modeling kernel
    - not 100% robust with solid ops but works
    - I (dan) am intimately familiar with its operations and pitfalls
    - offers some alternative execution environments (as a [plugin](https://www.rhino3d.com/inside), as a [REST API](https://www.rhino3d.com/compute) 

 I think it's important to consider what features we actually need from a kernel for this project. Personally my take is that we need: 
 
 - Functions that are usable, headlessly, as a library, in native general purpose languages. 
 - Export for data to a common geometry format for visualization, which can be done separately.
 - Robust support for basic solid & surface modeling features, though not necessarily advanced ones. 
 
 So far I think the option that meets the most of these checkboxes is CGAL. 
 
 
 
 
