A shape language.

## Some design goals:
- Usable as an IR between geometric implementations.
- Easy to prove properties or extract constraints & equivalences.
- Easy to automatically manipulate and transform.
- Reasonably matches the structure of real CAD? (2D sketches -> extrusions?)

## Questions:
- Symbolic "constraint" operations? (see #constraints below) Should these be the basis instead?
- How many constructors for the same object? e.g. can make an Arc by Start/End/Radius/Direction or by Center/Radius/θ1/θ2. Is it better to:
  - Include both of these to increase the space of easily-findable parametrizations?
  - Only have one canonical form of each constructor to decrease load on any synthesis we do?

# Base

Basic operations include:
   - `Vec2 / Vec3`                 (Points)
   - `Line / Circle / Polyline`    (2D)
   - `Extrude`                     (Projections)
   - `Union / Difference`          (CSG ops)
   - `Translate / Rotate / Scale`  (Transforms)


This is basically Caddy + References. For example:

   ```ml
   let base = 0;
   let far = 5;
   (* construct rectangle *)
   let p1 = vec2 base base;
   let p2 = vec2 far base;
   let p3 = vec2 far far;
   let p4 = vec2 base far;
   let pl = list p1 p2 p3 p4;
   let ex = extrude pl (vec3 0 0 far);
   (* transform and modify 3d meshes *)
   let e2 = translate ex (vec3 far far 0);
   let mid = / (+ far base) 2.0;
   let e3 = scale (vec3 mid mid mid) (1.5) (translate ex (vec3 mid mid 0));
   let all = union (set p1 ex e2 e3);
   return all;
   ```

# Deconstruction

   `Faces / Edges / Vertices`

   Example:
   ```ml
   let box = ex; (* from above *)
   let edges = edges box;
   ```

   These come out in some defined order.

   ```ml
   let e0 = pick edges (list 0);
   let v0 = vertices e0;
   let m0 = average v0;
   let e2 = pick edges (list 2);
   let v2 = vertices e2;
   let m2 = average v2;
   let l = line m0 m2;
   ```

# Constraints

   The thing about deconstruction is that it's prescriptive, not declarative.
   i.e. this draws a line at the midpoint of the two vertical faces of the cuboid
   in question, but it
   - runs into pain with selection (what if the edges come out in a different order and indexing breaks?)
   - is actually representing a semantic constraint (place at half height of x).

   Can we introduce 'spec' portions inside the program?
   These are holes which are implementable in any which way the synthesizer chooses.

   ```ml
   let l = line A B; (* capital == symbolic variables *)
   constrain line (proportional (vec3 0 0 1) ex 0.5);
   ```
   This line isn't fully constrained, but we can pick reasonable values for it. Further constraints can fully-define it.

   The grammar of this might be as follows:

   ```
   constrain  := constrain <variable> <constraint>
   constraint := proportional <direction : vec3> <reference : cad> <percentage : Real>
               | tangential
               | concentric
               | flush
               | ... etc.
   ```

# Loops

   `Tabulate` (both lists and sets)

   Example (rewritten from above):
   ```ml
   let midpts = tabulate 0 2 2 (i =>
    let ei = pick edges i;
    let vi = vertices ei;
    return average vi;
   );
   let l = line midpts;
   ```

   We can have same thing but returns a set.
   ```ml
   let stairs = tabulate_set 0 10 1 (i =>
     let h = * i (vec3 0 1 1);
     let d = vec3 4 1 0.25;
     return box h (+ h d)
   );
   ```

   If we then dragged a stair, say, we could map which iteration of the function was responsible for said stair, and adjust the parameters accordingly.
