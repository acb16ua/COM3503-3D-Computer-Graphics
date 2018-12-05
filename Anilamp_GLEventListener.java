/* I declare that this code is my own work unless stated otherwise*/
/* Author: Uddhav Agarwal*/
/* Email: uagarwal1@sheffield.ac.uk */

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.concurrent.ThreadLocalRandom;

public class Anilamp_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

  public Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }


  //######## INITIALISATION ########//
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  //######## DRAW ########//
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    //wall.dispose(gl);
    window.dispose(gl);
    sphere.dispose(gl);
    wall1.dispose(gl);
    wall2.dispose(gl);
    wall3.dispose(gl);
    wall4.dispose(gl);
  }

  //######## INTERACTION ########//

   private boolean animation = false;
   private double savedTime = 2;

   public void startAnimation() {
     animation = true;
     startTime = getSeconds()-savedTime;
   }

   public void stopAnimation() {
     animation = false;
     double elapsedTime = getSeconds()-startTime;
     savedTime = elapsedTime;
   }

   public void incXPosition() {
     xPosition += 0.5f;
     if (xPosition>5f) xPosition = 5f;
     updateX();
   }

   public void decXPosition() {
     xPosition -= 0.5f;
     if (xPosition<-5f) xPosition = -5f;
     updateX();
   }

   public void randomPose() {
     double elapsedTime = getSeconds()-startTime;
     rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
     float rotateAngle1 = -1f-35f*(float)Math.sin(elapsedTime);
     float rotateAngle2 = -1f-35f*(float)Math.sin(elapsedTime);
     randomLowerArm.setTransform(Mat4Transform.rotateAroundZ(rotateAngle1));
     randomLowerArm.update();
     randomLowerArm2.setTransform(Mat4Transform.rotateAroundZ(rotateAngle1));
     randomLowerArm2.update();
     randomUpperArm.setTransform(Mat4Transform.rotateAroundZ(rotateAngle2));
     randomUpperArm.update();
     randomUpperArm2.setTransform(Mat4Transform.rotateAroundZ(rotateAngle2));
     randomUpperArm2.update();

     twoBranchRoot.update();
   }

   private void updateX() {
     translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
     translateX.update(); // IMPORTANT – the scene graph has changed
   }



  //######## THE SCENE ########//

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, cube, wall1, wall2, wall3, wall4, window, lampBaseModel, globeBodyModel, globeBaseModel, cellphoneModel;
  private Model lampLowerArm2Model, lampHingeSocketModel, lampUpperArm1Model, windowFrameModel, plantModel, plantPotModel;
  private Light light;
  //private LampLight lampLight;
  private SGNode twoBranchRoot, windowFrameRoot;
  private TransformNode translateX, randomLowerArm, randomLowerArm2, randomUpperArm, randomUpperArm2, rotateAll, rotateUpper, rotateUpper2, rotateUpper3;
  private TransformNode rotateLight, rotatePhone, translateToTopOfTable1, makePlant, makePlantPot, rotateBase, translateFrame, fixedLowerArm1, fixedLowerArm2, fixedUpperArm1, fixedUpperArm2;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  private int randomLowerArmA = 0;
  private int randomUpperArmA = 0;

  private void initialise(GL3 gl) {


    //######## LOADING THE TEXTURES ########//
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/marble_01_diff_1k.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/Wood_002_COLOR.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/Wood_002_ROUGH.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/marble_01_spec_1k.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/winter_clouds-wallpaper.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/lamp_diff.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/lamp_spec.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/white_plaster_02_diff_1k.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/white_plaster_02_spec_1k.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/ear0xuu2.jpg");
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/ear0xuu2_specular.jpg");
    int[] textureId11 = TextureLibrary.loadTexture(gl, "textures/phone2.jpg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/hill3.jpg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/concrete_diff_1k.jpg");
    int[] textureId14 = TextureLibrary.loadTexture(gl, "textures/cactus.jpg");

    light = new Light(gl);
    //lampLight = new LampLight(gl);
    light.setCamera(camera);
    //lampLight.setCamera(camera);


    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0, textureId3);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.01f, 0.01f, 0.01f), new Vec3(1.0f, 1.0f, 1.0f), 2f);
    modelMatrix = Mat4Transform.scale(5.5f,1f,7.5f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(5.25f,3.75f,-16f*0.5f), modelMatrix);
    wall1 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7, textureId8);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.01f, 0.01f, 0.01f), new Vec3(1.0f, 1.0f, 1.0f), 2f);
    modelMatrix = Mat4Transform.scale(10.5f,1f,4);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(2.75f,9f,-16f*0.5f), modelMatrix);
    wall2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7, textureId8);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.01f, 0.01f, 0.01f), new Vec3(1.0f, 1.0f, 1.0f), 2f);
    modelMatrix = Mat4Transform.scale(5.5f,1f,7.5f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-5.25f,7.25f,-16f*0.5f), modelMatrix);
    wall3 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7, textureId8);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.01f, 0.01f, 0.01f), new Vec3(1.0f, 1.0f, 1.0f), 2f);
    modelMatrix = Mat4Transform.scale(10.5f,1f,4);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-2.75f,2f,-16f*0.5f), modelMatrix);
    wall4 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7, textureId8);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shaderWindow = new Shader(gl, "vs_window_05.txt", "fs_window_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(17,1f,11);
    //modelMatrix = Mat4.multiply(Mat4Transform.scale(16f,1f,16f), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,4.5f,-19f), modelMatrix);
    window = new Model(gl, camera, light, shaderWindow, material, modelMatrix, mesh, textureId4, textureId12);

    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6);
    globeBodyModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId9, textureId10);
    plantModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId14);

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
    lampBaseModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6);
    globeBaseModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6);
    cellphoneModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId11);
    windowFrameModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId11);
    plantPotModel = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId13);

    twoBranchRoot = new NameNode("tableAndStuff");
    windowFrameRoot = new NameNode("windowFrameStructure");
    translateFrame = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));

    TransformNode translatePane1 = new TransformNode("translate(0f,3.75f,-8f)",Mat4Transform.translate(0f,3.75f,-8f));
    NameNode windowPane1 = new NameNode("windowPane1");
    Mat4 m = Mat4Transform.scale(5f,0.3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeWindowPane1 = new TransformNode("scale(5f,0.3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode windowPaneModel1 = new ModelNode("windowPane1", cube);

    TransformNode translatePane2 = new TransformNode("translate(2.5f,3.75f,-8f)",Mat4Transform.translate(2.5f,3.75f,-8f));
    NameNode windowPane2 = new NameNode("windowPane2");
    m = Mat4Transform.scale(0.3f,3.3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeWindowPane2 = new TransformNode("scale(0.3f,3.3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode windowPaneModel2 = new ModelNode("translatePane2", cube);

    TransformNode translatePane3 = new TransformNode("translate(0f,6.75f,-8f)",Mat4Transform.translate(0f,6.75f,-8f));
    NameNode windowPane3 = new NameNode("windowPane3");
    m = Mat4Transform.scale(5f,0.3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeWindowPane3 = new TransformNode("scale(5f,0.3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode windowPaneModel3 = new ModelNode("translatePane3", cube);

    TransformNode translatePane4 = new TransformNode("translate(-2.5f,3.75f,-8f)",Mat4Transform.translate(-2.5f,3.75f,-8f));
    NameNode windowPane4 = new NameNode("windowPane4");
    m = Mat4Transform.scale(0.3f,3.3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeWindowPane4 = new TransformNode("scale(0.3f,3.3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode windowPaneModel4 = new ModelNode("translatePane4", cube);

    TransformNode translateTable = new TransformNode("translate(0f,0.05f,-5f)",Mat4Transform.translate(0f,0.05f,-5f));
    rotatePhone = new TransformNode("rotateAroundZ(-45f)",Mat4Transform.rotateAroundY(-45f));
    rotateBase = new TransformNode("rotateAroundY(45f)",Mat4Transform.rotateAroundY(45f));
    NameNode tableBase = new NameNode("table_base");
    m = Mat4Transform.scale(7f,0.2f,5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,15.5f,0));
    TransformNode makeTableBase = new TransformNode("scale(7f,0.2f,5f); translate(0,15.5f,0)", m);
    ModelNode tableBaseModel = new ModelNode("tableBase", cube);

    TransformNode translateLegUnderTable1 = new TransformNode("translate(-3f,0.05f,-2f)",Mat4Transform.translate(-3f,0.05f,-2f));
    NameNode tableLeg1 = new NameNode("table_leg");
    m = Mat4Transform.scale(0.3f,3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeTableLeg1 = new TransformNode("scale(0.3f,3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode tableLegModel1 = new ModelNode("tableLeg1", cube);

    TransformNode translateLegUnderTable2 = new TransformNode("translate(3f,0.05f,-2f)",Mat4Transform.translate(3f,0.05f,-2f));
    NameNode tableLeg2 = new NameNode("table_leg");
    m = Mat4Transform.scale(0.3f,3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeTableLeg2 = new TransformNode("scale(0.3f,3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode tableLegModel2 = new ModelNode("tableLeg2", cube);

    TransformNode translateLegUnderTable3 = new TransformNode("translate(-3f,0.05f,2f)",Mat4Transform.translate(-3f,0.05f,2f));
    NameNode tableLeg3 = new NameNode("table_leg");
    m = Mat4Transform.scale(0.3f,3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeTableLeg3 = new TransformNode("scale(0.3f,3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode tableLegModel3 = new ModelNode("tableLeg3", cube);

    TransformNode translateLegUnderTable4 = new TransformNode("translate(3f,0.05f,2f)",Mat4Transform.translate(3f,0.05f,2f));
    NameNode tableLeg4 = new NameNode("table_leg");
    m = Mat4Transform.scale(0.3f,3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeTableLeg4 = new TransformNode("scale(0.3f,3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode tableLegModel4 = new ModelNode("tableLeg4", cube);

    TransformNode translateToTopOfTable1 = new TransformNode("translate(-2f,3.2f,-2f)",Mat4Transform.translate(-2f,3.2f,-2f));
    NameNode plantPot = new NameNode("plantPot");
    m = Mat4Transform.scale(0.75f,0.5f,0.75f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makePlantPot = new TransformNode("scale(0.75f,0.5f,0.75f); translate(0,0.5f,0)", m);
    ModelNode plantPotThing = new ModelNode("plantPot", plantPotModel);

    //TransformNode translateToTopOfGlobeBase = new TransformNode("translate(0,0.3,0)",Mat4Transform.translate(0,3.2f,0));
    NameNode plant = new NameNode("plant");
    m = Mat4Transform.scale(0.5f,1.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makePlant = new TransformNode("scale(0.5f,1.5f,0.5f); translate(0,0.5f,0)", m);
    ModelNode plantThing = new ModelNode("plant", plantModel);

    TransformNode translateToTopOfTable2 = new TransformNode("translate(2f,3.2f,-2f)",Mat4Transform.translate(2f,3.2f,-2f));
    NameNode globeBase = new NameNode("globeBase");
    m = Mat4Transform.scale(0.5f,0.05f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeGlobeBase = new TransformNode("scale(0.5f,0.05f,0.5f); translate(0,0.5f,0)", m);
    ModelNode globeBaseThing = new ModelNode("globeBase", globeBaseModel);

    //TransformNode translateToTopOfGlobeBase = new TransformNode("translate(0,0.3,0)",Mat4Transform.translate(0,3.2f,0));
    NameNode globeBody = new NameNode("globeBody");
    m = Mat4Transform.scale(1f,1f,1f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeGlobeBody = new TransformNode("scale(1f,1f,1f); translate(0,0.5f,0)", m);
    ModelNode globeBodyThing = new ModelNode("globeBody", globeBodyModel);

    TransformNode translateToTopOfTable3 = new TransformNode("translate(2f,3.2f,1.5f)",Mat4Transform.translate(2f,3.2f,1.5f));
    NameNode cell_phone = new NameNode("cellphone");
    m = Mat4Transform.scale(0.35f,0.05f,0.8f);
    //m = Mat4.multiply(m, Mat4Transform.rotateAroundY());
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeCellphone = new TransformNode("scale(0.35f,0.05f,0.8f); translate(0,0.5f,0)", m);
    ModelNode cellphone = new ModelNode("cell_phone", cellphoneModel);

    TransformNode translateLampBaseOnTable = new TransformNode("translate(-2f,3.2f,1.4f)",Mat4Transform.translate(-2f,3.2f,1.4f));
    NameNode lampBase = new NameNode("lamp_base");
    m = Mat4Transform.scale(0.5f,0.05f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampBase = new TransformNode("scale(0.5f,0.05f,0.5f); translate(0,0.5f,0)", m);
    ModelNode lampBaseThing = new ModelNode("lampBase", lampBaseModel);

    TransformNode transLa1onBase = new TransformNode("translate(0,0.05f,-0.06f)",Mat4Transform.translate(0,0.05f,-0.06f));
    //rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(45f));
    fixedLowerArm1 = new TransformNode("rotateAroundZ(45f)",Mat4Transform.rotateAroundZ(45f));
    randomLowerArm = new TransformNode("rotateAroundZ(randomLowerArmA)", Mat4Transform.rotateAroundZ(randomLowerArmA));
    NameNode lampLowerArm1 = new NameNode("lamp_lower_arm1");
    m = Mat4Transform.scale(0.04f,1.8f,0.04f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampLowerArm1 = new TransformNode("scale(0.04f,1.8f,0.04f); translate(0,0.5f,0)", m);
    ModelNode lampLowerArm1Thing = new ModelNode("lampLowerArm1", sphere);

    TransformNode transLa2onBase = new TransformNode("translate(0,0.05f,0.06f)",Mat4Transform.translate(0,0.05f,0.06f));
    //rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(45f));
    fixedLowerArm2 = new TransformNode("rotateAroundZ(45f)",Mat4Transform.rotateAroundZ(45f));
    randomLowerArm2 = new TransformNode("rotateAroundZ(randomLowerArmA)", Mat4Transform.rotateAroundZ(randomLowerArmA));
    NameNode lampLowerArm2 = new NameNode("lamp_lower_arm2");
    m = Mat4Transform.scale(0.04f,1.8f,0.04f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampLowerArm2 = new TransformNode("scale(0.04f,1.8f,0.04f); translate(0,0.5f,0)", m);
    ModelNode lampLowerArm2Thing = new ModelNode("lampLowerArm2", sphere);

    TransformNode transJointonArm = new TransformNode("translate(0,1.6f,0.08f)",Mat4Transform.translate(0,1.6f,0.08f));
    NameNode lampHingeSocket = new NameNode("lamp_join_sphere");
    m = Mat4Transform.scale(0.3f,0.3f,0.3f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampHingeSocket = new TransformNode("scale(0.3f,0.3f,0.3f); translate(0,0.5f,0)", m);
    ModelNode lampHingeSocketThing = new ModelNode("lampHingeSocket", sphere);

    TransformNode transUa1onJoint = new TransformNode("translate(-0.1f,0.1f,-0.06f)",Mat4Transform.translate(-0.1f,0.1f,-0.06f));
    //rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(-45f));
    fixedUpperArm1 = new TransformNode("rotateAroundZ(-90f)",Mat4Transform.rotateAroundZ(-90f));
    randomUpperArm = new TransformNode("rotateAroundZ(randomUpperArmA)", Mat4Transform.rotateAroundZ(randomUpperArmA));
    NameNode lampUpperArm1 = new NameNode("lamp_upper_arm1");
    m = Mat4Transform.scale(0.04f,1.8f,0.04f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampUpperArm1 = new TransformNode("scale(0.04f,1.8f,0.04f); translate(0,0.5f,0)", m);
    ModelNode lampUpperArm1Thing = new ModelNode("lampUpperArm1", sphere);

    TransformNode transUa2onJoint = new TransformNode("translate(-0.1f,0.1f,0.06f)",Mat4Transform.translate(-0.1f,0.1f,0.06f));
    //rotateUpper2 = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(-45f));
    fixedUpperArm2 = new TransformNode("rotateAroundZ(-90f)",Mat4Transform.rotateAroundZ(-90f));
    randomUpperArm2 = new TransformNode("rotateAroundZ(randomUpperArmA)", Mat4Transform.rotateAroundZ(randomUpperArmA));
    NameNode lampUpperArm2 = new NameNode("lamp_upper_arm2");
    m = Mat4Transform.scale(0.04f,1.8f,0.04f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampUpperArm2 = new TransformNode("scale(0.04f,1.8f,0.04f); translate(0,0.5f,0)", m);
    ModelNode lampUpperArm2Thing = new ModelNode("lampUpperArm2", sphere);

    TransformNode transCoverOnLamp = new TransformNode("translate(-0.2f,1.8f,-0.075f)",Mat4Transform.translate(-0.2f,1.8f,-0.075f));
    rotateUpper3 = new TransformNode("rotateAroundZ(rotateUpperAngle)",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    rotateLight = new TransformNode("rotateAroundZ(-90f)",Mat4Transform.rotateAroundZ(-90f));
    NameNode lampLightCover = new NameNode("lamp_light_cover");
    m = Mat4Transform.scale(0.5f,0.5f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampLightCover = new TransformNode("scale(0.5f,0.5f,0.5f); translate(0,0.5f,0)", m);
    ModelNode lampLightCoverThing = new ModelNode("lampLightCover", sphere);

    TransformNode transLightBulbOnLamp = new TransformNode("translate(0,0.4f,0)",Mat4Transform.translate(0,0.4f,0));
    //rotateUpper3 = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    NameNode lampLightBulb = new NameNode("lamp_light");
    m = Mat4Transform.scale(0.2f,0.2f,0.2f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLampLightBulb = new TransformNode("scale(0.2f,0.2f,0.2f); translate(0,0.5f,0)", m);
    ModelNode lampLightBulbThing = new ModelNode("lampLightBulb", sphere);

    //######## SCENE GRAPH FOR THE WINDOW FRAME ########//
    windowFrameRoot.addChild(translateFrame);
      translateFrame.addChild(translatePane1);
        translatePane1.addChild(windowPane1);
          windowPane1.addChild(makeWindowPane1);
            makeWindowPane1.addChild(windowPaneModel1);
      translateFrame.addChild(translatePane2);
        translatePane2.addChild(windowPane2);
          windowPane2.addChild(makeWindowPane2);
            makeWindowPane2.addChild(windowPaneModel2);
      translateFrame.addChild(translatePane3);
        translatePane3.addChild(windowPane3);
          windowPane3.addChild(makeWindowPane3);
            makeWindowPane3.addChild(windowPaneModel3);
      translateFrame.addChild(translatePane4);
        translatePane4.addChild(windowPane4);
          windowPane4.addChild(makeWindowPane4);
            makeWindowPane4.addChild(windowPaneModel4);

    //######## SCENE GRAPH FOR THE TABLE AND EVERYTHING ON IT ########//
    twoBranchRoot.addChild(translateX);
      translateX.addChild(translateTable);
      translateTable.addChild(tableBase);
        tableBase.addChild(makeTableBase);
          makeTableBase.addChild(tableBaseModel);
        tableBase.addChild(translateLegUnderTable1);
          translateLegUnderTable1.addChild(makeTableLeg1);
          makeTableLeg1.addChild(tableLegModel1);
        tableBase.addChild(translateLegUnderTable2);
          translateLegUnderTable2.addChild(makeTableLeg2);
          makeTableLeg2.addChild(tableLegModel2);
        tableBase.addChild(translateLegUnderTable3);
          translateLegUnderTable3.addChild(makeTableLeg3);
          makeTableLeg3.addChild(tableLegModel3);
        tableBase.addChild(translateLegUnderTable4);
          translateLegUnderTable4.addChild(makeTableLeg4);
          makeTableLeg4.addChild(tableLegModel4);
        tableBase.addChild(translateLampBaseOnTable);
          translateLampBaseOnTable.addChild(rotateBase);
            rotateBase.addChild(lampBase);
            lampBase.addChild(makeLampBase);
              makeLampBase.addChild(lampBaseThing);
            lampBase.addChild(transLa1onBase);
              transLa1onBase.addChild(fixedLowerArm1);
                fixedLowerArm1.addChild(randomLowerArm);
                randomLowerArm.addChild(lampLowerArm1);
                  lampLowerArm1.addChild(makeLampLowerArm1);
                    makeLampLowerArm1.addChild(lampLowerArm1Thing);
            lampBase.addChild(transLa2onBase);
              transLa2onBase.addChild(fixedLowerArm2);
                fixedLowerArm2.addChild(randomLowerArm2);
                  randomLowerArm2.addChild(lampLowerArm2);
                  lampLowerArm2.addChild(makeLampLowerArm2);
                    makeLampLowerArm2.addChild(lampLowerArm2Thing);
                  lampLowerArm1.addChild(transJointonArm);
                    transJointonArm.addChild(lampHingeSocket);
                      lampHingeSocket.addChild(makeLampHingeSocket);
                        makeLampHingeSocket.addChild(lampHingeSocketThing);
                      lampHingeSocket.addChild(transUa1onJoint);
                        transUa1onJoint.addChild(fixedUpperArm1);
                          fixedUpperArm1.addChild(randomUpperArm);
                          randomUpperArm.addChild(lampUpperArm1);
                            lampUpperArm1.addChild(makeLampUpperArm1);
                              makeLampUpperArm1.addChild(lampUpperArm1Thing);
                      lampHingeSocket.addChild(transUa2onJoint);
                        transUa2onJoint.addChild(fixedUpperArm2);
                          fixedUpperArm2.addChild(randomUpperArm2);
                          randomUpperArm2.addChild(lampUpperArm2);
                            lampUpperArm2.addChild(makeLampUpperArm2);
                              makeLampUpperArm2.addChild(lampUpperArm2Thing);
                            lampUpperArm2.addChild(transCoverOnLamp);
                              transCoverOnLamp.addChild(rotateLight);
                                rotateLight.addChild(lampLightCover);
                                  lampLightCover.addChild(makeLampLightCover);
                                    makeLampLightCover.addChild(lampLightCoverThing);
                                  lampLightCover.addChild(transLightBulbOnLamp);
                                    transLightBulbOnLamp.addChild(lampLightBulb);
                                      lampLightBulb.addChild(makeLampLightBulb);
                                        makeLampLightBulb.addChild(lampLightBulbThing);
        tableBase.addChild(translateToTopOfTable2);
          translateToTopOfTable2.addChild(globeBase);
            globeBase.addChild(makeGlobeBase);
              makeGlobeBase.addChild(globeBaseThing);
            globeBase.addChild(makeGlobeBody);
                makeGlobeBody.addChild(globeBodyThing);
        tableBase.addChild(translateToTopOfTable3);
          translateToTopOfTable3.addChild(rotatePhone);
            rotatePhone.addChild(cell_phone);
              cell_phone.addChild(makeCellphone);
                makeCellphone.addChild(cellphone);
        tableBase.addChild(translateToTopOfTable1);
          translateToTopOfTable1.addChild(plantPot);
            plantPot.addChild(makePlantPot);
              makePlantPot.addChild(plantPotThing);
            plantPot.addChild(plant);
              plant.addChild(makePlant);
                makePlant.addChild(plantThing);

    twoBranchRoot.update();
    windowFrameRoot.update();
    twoBranchRoot.print(0, false);
    windowFrameRoot.print(0, false);
    //System.exit(0);

  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());
    light.render(gl);
    floor.render(gl);
    window.render(gl);
    wall1.render(gl);
    wall2.render(gl);
    wall3.render(gl);
    wall4.render(gl);
    if (animation) randomPose();
    twoBranchRoot.draw(gl);
    windowFrameRoot.draw(gl);
    moveClouds(gl);
  }


  private void moveClouds(GL3 gl) {
    double elapsedTime = getSeconds() - startTime;
    double t = elapsedTime*0.1;
    float offsetX = (float)(t - Math.floor(t));
    float offsetY = 0.0f;
    window.shader.setFloat(gl, "offset", offsetX, offsetY );
  }



  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 7.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
    //return new Vec3(5f,3.4f,5f);
  }

  // ***************************************************
  /* TIME
   */

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }


}
