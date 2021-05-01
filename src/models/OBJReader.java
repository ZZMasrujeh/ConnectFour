package models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import main.ErrorWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OBJReader {
    /**
     * In blender, files can be exported by clicking File -> Export -> Wavefront(.obj) and then ensure that in geometry
     * the following options are selected: Write Normals, Include UVs, Triangulate Faces.
     * @param filename location of the .obj file to be read.
     */
    public static TriangleMesh getMesh(String filename){
        TriangleMesh t = new TriangleMesh();

        File file = new File(filename);
        Scanner s = null;
        try {
            s = new Scanner(file);
        } catch (FileNotFoundException e) {
            new ErrorWindow("File " + filename + " not found");
        }

        while (s.hasNextLine()) {
            String line = s.nextLine();
            //vertex
            if (line.charAt(0) == 'v' && line.charAt(1) == ' ') {
                String[] lineA = line.split(" ");
                t.getPoints().addAll(Float.parseFloat(lineA[1]), Float.parseFloat(lineA[2]), Float.parseFloat(lineA[3]));
            }
            //vertex texCoord
            if (line.charAt(0) == 'v' && line.charAt(1) == 't') {
                String[] lineA = line.split(" ");
                t.getTexCoords().addAll(Float.parseFloat(lineA[1]), Float.parseFloat(lineA[2]));
            }
            //vertex normal
            if (line.charAt(0) == 'v' && line.charAt(1) == 'n') {
                String[] lineA = line.split(" ");
                t.getNormals().addAll(Float.parseFloat(lineA[1]), Float.parseFloat(lineA[2]), Float.parseFloat(lineA[3]));
            }
            //faces
            if (line.charAt(0) == 'f') {
                String[] lineA = line.split(" ");
                for (int i = 1; i < lineA.length; i++) {
                    String[] subLine = lineA[i].split("/");
                    t.getFaces().addAll(
                            Integer.parseInt(subLine[0]) - 1,
                            Integer.parseInt(subLine[2]) - 1,
                            Integer.parseInt(subLine[1]) - 1
                    );
                }
            }
        }

        t.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);

        return t;
    }

    public static MeshView getMeshView(Color col, String filename){
        TriangleMesh t = getMesh(filename);

        MeshView mv = new MeshView(t);
        mv.setMaterial(material(col));

        return mv;
    }

    /**
     * @param diffuseMap location of the photo to be used as a diffuse map
     * @return material with diffuse map
     */
    public static PhongMaterial material(String diffuseMap) {
        PhongMaterial mat = new PhongMaterial();

        try {
            FileInputStream fis = new FileInputStream(diffuseMap);
            Image i = new Image(fis);
            mat.setDiffuseMap(i);
        } catch (FileNotFoundException e) {
            new ErrorWindow("File " + diffuseMap + " not found");
        }

        return mat;
    }
    /**
     * @param color the desired color of a model
     * @return material with desired color
     */
    public static PhongMaterial material(Color color) {
        return new PhongMaterial(color);
    }
}
