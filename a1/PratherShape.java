package a1;

import tage.*;
import tage.shapes.*;

// custom shape class, creates an octahedron
public class PratherShape extends ManualObject { 

 private float[] vertices = new float[] {
    -1.0f, 0f, 1.0f,    1.0f, 0f, 1.0f,     0.0f, 1.5f, 0.0f,
    1.0f, 0f, 1.0f,     1.0f, 0f, -1.0f,   0.0f, 1.5f, 0.0f, 
    1.0f, 0f, -1.0f,   -1.0f, 0f, -1.0f,   0.0f, 1.5f, 0.0f,
    -1.0f, 0f, -1.0f,   -1.0f, 0f, 1.0f,    0.0f, 1.5f, 0.0f,
    
    1.0f, 0f, -1.0f,    -1.0f, 0f, -1.0f,     0.0f, -1.5f, 0.0f, 
    -1.0f, 0f, -1.0f,     -1.0f, 0f, 1.0f,   0.0f, -1.5f, 0.0f, 
    -1.0f, 0f, 1.0f,   1.0f, 0f, 1.0f,   0.0f, -1.5f, 0.0f, 
    1.0f, 0f, 1.0f,   1.0f, 0f, -1.0f,    0.0f, -1.5f, 0.0f };

 private float[] texcoords = new float[] {
    0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f, 

    0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f, 
    0.0f, 0.0f,   1.0f, 0.0f,    0.5f, 1.0f };

 private float[] normals = new float[] {
    0.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f,      0.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 0.0f,     1.0f, 1.0f, 0.0f,      1.0f, 1.0f, 0.0f,
    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,
    -1.0f, 1.0f, 0.0f,   -1.0f, 1.0f, 0.0f,     -1.0f, 1.0f, 0.0f,

    0.0f, 1.0f, 1.0f,     0.0f, 1.0f, 1.0f,      0.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 0.0f,     1.0f, 1.0f, 0.0f,      1.0f, 1.0f, 0.0f,
    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,
    -1.0f, 1.0f, 0.0f,   -1.0f, 1.0f, 0.0f,     -1.0f, 1.0f, 0.0f };

 public PratherShape() {
  super();
  setNumVertices(24);
  setVertices(vertices);
  setTexCoords(texcoords);
  setNormals(normals);
 }
}
