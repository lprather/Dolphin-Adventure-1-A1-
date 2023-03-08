package a1;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import org.joml.*;
import tage.*;
import tage.input.*;
import tage.input.action.*;
import tage.rml.Angle;
import tage.shapes.*;

public class MyGame extends VariableFrameRateGame {

  private static Engine engine;

  private double lastFrameTime, currFrameTime, elapsTime, timeDiff;
  private double timeOffAv = 0.0;

  private InputManager im;
  private GameObject dol, cub, sphere, tor, x, y, z, cust;
  private ObjShape dolS, cubS, sphereS, torS, linxS, linyS, linzS, custS;
  private TextureImage doltx, brick, wood, forest, abst;
  private Light light1;

  private int numPrizes;
  private int goX, goY, goZ, camX, camY, camZ;
  private boolean camOnDol = true, avHasObj = false;

  private Random rand = new Random();
  private ArrayList<GameObject> gameObjs = new ArrayList<GameObject>();

  private Vector3f loc, camLoc, fwd, up, right, newLocation;
  private Matrix4f rot;
  private Camera cam;

  private Vector4f location, fwdDir;

  public MyGame() {
    super();
  }

  public static void main(String[] args) {
    MyGame game = new MyGame();
    engine = new Engine(game);
    game.initializeSystem();
    game.game_loop();
  }

  @Override
  public void loadShapes() {
    dolS = new ImportedModel("dolphinHighPoly.obj");
    cubS = new Cube();
    sphereS = new Sphere();
    torS = new Torus(0.5f, 0.2f, 48);
    custS = new PratherShape();
    linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(100f, 0f, 0f));
    linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 100f, 0f));
    linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 100f));
  }

  @Override
  public void loadTextures() {
    doltx = new TextureImage("Dolphin_HighPolyUV.png");
    brick = new TextureImage("brick1.jpg");
    wood = new TextureImage("wood1.jpg");
    abst = new TextureImage("abstract1.jpg");
    forest = new TextureImage("forest1.jpg");
  }

  @Override
  public void buildObjects() {
    Matrix4f initialTranslation, initialScale, initialRotation;

    // build dolphin
    dol = new GameObject(GameObject.root(), dolS, doltx);
    initialTranslation = (new Matrix4f()).translation(-1f, 0f, 1f);
    initialScale = (new Matrix4f()).scaling(3.0f);
    dol.setLocalTranslation(initialTranslation);
    dol.setLocalScale(initialScale);
    initialRotation =
      (new Matrix4f()).rotationY((float) java.lang.Math.toRadians(135.0f));
    dol.setLocalRotation(initialRotation);

    // build cube randomly in space (within bounds of 0,50 for each x,y,z)
    cub = new GameObject(GameObject.root(), cubS, abst);
    initialTranslation =
      (new Matrix4f()).translation(
          rand.nextInt(50),
          rand.nextInt(50),
          rand.nextInt(50)
        );
    initialScale = (new Matrix4f()).scaling(0.25f);
    cub.setLocalTranslation(initialTranslation);
    cub.setLocalScale(initialScale);
    gameObjs.add(cub);

    // build sphere randomly in space (within bounds of -50,50 for each x,y,z)
    sphere = new GameObject(GameObject.root(), sphereS, wood);
    initialTranslation =
      (new Matrix4f()).translation(
          rand.nextInt(100) - 50,
          rand.nextInt(100) - 50,
          rand.nextInt(100) - 50
        );
    initialScale = (new Matrix4f()).scaling(0.5f);
    sphere.setLocalTranslation(initialTranslation);
    sphere.setLocalScale(initialScale);
    gameObjs.add(sphere);

    // build torus randomly in space (within bounds of -100,100 for each x,y,z)
    tor = new GameObject(GameObject.root(), torS, brick);
    initialTranslation =
      (new Matrix4f()).translation(
          rand.nextInt(200) - 100,
          rand.nextInt(200) - 100,
          rand.nextInt(200) - 100
        );
    tor.setLocalTranslation(initialTranslation);
    initialScale = (new Matrix4f()).scaling(0.75f);
    tor.setLocalScale(initialScale);
    gameObjs.add(tor);

    // add X,Y,Z axes
    x = new GameObject(GameObject.root(), linxS);
    y = new GameObject(GameObject.root(), linyS);
    z = new GameObject(GameObject.root(), linzS);
    (x.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
    (y.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
    (z.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

    // build custom shape and place it at (10,10,10) with scale 1.5
    cust = new GameObject(GameObject.root(), custS, forest);
    initialTranslation = (new Matrix4f()).translation(10,10,10);
    cust.setLocalTranslation(initialTranslation);
    initialScale = (new Matrix4f()).scaling(1.5f);
    cust.setLocalScale(initialScale);
    cust.getRenderStates().hasLighting(true);
  }

  @Override
  public void initializeLights() {
    Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
    light1 = new Light();
    light1.setLocation(new Vector3f(5.0f, 10.0f, 5.0f));
    (engine.getSceneGraph()).addLight(light1);
  }

  @Override
  public void initializeGame() {
    lastFrameTime = System.currentTimeMillis();
    currFrameTime = System.currentTimeMillis();
    elapsTime = 0.0;
    (engine.getRenderSystem()).setWindowDimensions(1900, 1000);

    // ------------- positioning the camera -------------
    (engine.getRenderSystem().getViewport("MAIN").getCamera()).setLocation(
        new Vector3f(0, 0, 5)
      );
    positionCameraOnAv();

    im = engine.getInputManager();
    FwdAction fwdActionG = new FwdAction(this); // forward action for gamepads
    FwdAction fwdActionK = new FwdAction(this, 0); // forward action for keyboards
    FwdAction backActionK = new FwdAction(this, 1); // backward action for keyboards

    YawAction yawActionG = new YawAction(this); // turn action for gamepads
    YawAction yawRActionK = new YawAction(this, 0); // turn right action for keyboards
    YawAction yawLActionK = new YawAction(this, 1); // turn left action for keyboards

    PitchAction pitchActionG = new PitchAction(this); // pitch action for gamepads
    PitchAction pitchUpActionK = new PitchAction(this, true); // pitch up action for keyboards
    PitchAction pitchDownActionK = new PitchAction(this, false); // pitch down action for keyboards

    ToggleCamAction toggleCamAction = new ToggleCamAction(this); // toggle cam action for gamepads and keyboards

    // Gamepad setup
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.Y,
      fwdActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.X,
      yawActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.RY,
      pitchActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._1,
      toggleCamAction,
      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY
    );

    // Keyboard setup
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.W,
      fwdActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.A,
      yawLActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.S,
      backActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.D,
      yawRActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.UP,
      pitchUpActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.DOWN,
      pitchDownActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.SPACE,
      toggleCamAction,
      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY
    );
  }

  @Override
  public void update() {
    lastFrameTime = currFrameTime;
    currFrameTime = System.currentTimeMillis();
    timeDiff = (currFrameTime - lastFrameTime) / 1000.0;
    elapsTime += timeDiff;

    // update inputs
    im.update((float) elapsTime);
    updateTimeOffAv(timeDiff);
    if ((int) timeOffAv > 15) {
      toggleCam();
    }

    // for each game object (prizes): if camCollision(go), numPrizes++
    for (int i = 0; i < gameObjs.size(); i++) {
      GameObject go = gameObjs.get(i);
      if (!avHasObj && camCollision(go)) { // if object and camera collide
        gameObjs.remove(i); // remove object from list of game objects
        numPrizes++; // add to score
        // remove the object from the scene graph when it collides with camera
        engine.getSceneGraph().removeGameObject(go);
      }
    }

    // if camera collides with the custom object,
    // any items being held are offloaded
    if (camCollision(cust)){
      avHasObj = false;
    }

    // build and set HUD
    String numPrizesStr = Integer.toString(numPrizes);
    String timeAway = Integer.toString((int) timeOffAv);
    String dispStr1 = "Score = " + numPrizesStr + ",  holding a prize: " + avHasObj;
    String dispStr2 = "Time away from dolphin: " + timeAway;
    Vector3f hud1Color = new Vector3f(1, 0, 0);
    Vector3f hud2Color = new Vector3f(0, 1, 0);
    (engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
    (engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 600, 15);
  }

  private void updateTimeOffAv(double time) {
    // update timer for how long the camera/player is off the dolphin
    if (!camOnDol) {
      timeOffAv += time;
    } else {
      timeOffAv = 0.0;
    }
  }

  public void positionCameraOnAv() {
    // positions camera in relation to dolphin
    // based on whether we want the camera on or off the dolphin
    cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
    if (camOnDol) { // align camera location and vectors with dolphin
      loc = dol.getWorldLocation();
      fwd = dol.getWorldForwardVector();
      up = dol.getWorldUpVector();
      right = dol.getWorldRightVector();
      cam.setU(right);
      cam.setV(up);
      cam.setN(fwd);
      cam.setLocation(loc.add(up.mul(1.3f)).add(fwd.mul(-1.5f)));
    } else { // put camera behind tail of dolphin (dismount!)
      loc = dol.getWorldLocation();
      fwd = dol.getWorldForwardVector();
      up = dol.getWorldUpVector();
      right = dol.getWorldRightVector();
      cam.setU(right);
      cam.setV(up);
      cam.setN(fwd);
      cam.setLocation(loc.add(up.mul(0.5f)).add(fwd.mul(-5f)));
    }
  }

  public void toggleCam() {
    // used in combination with positionCameraOnAv()
    // this method is called by ToggleCamAction.java
    if (camOnDol) {
      camOnDol = false;
    } else {
      camOnDol = true;
    }
    positionCameraOnAv();
  }

  private boolean camCollision(GameObject go) {
    // determine if there is a collision between the camera and game object
    boolean collision = false;
    loc = go.getWorldLocation();
    camLoc = cam.getLocation();

    goX = (int) loc.x();
    camX = (int) camLoc.x();

    goY = (int) loc.y();
    camY = (int) camLoc.y();

    goZ = (int) loc.z();
    camZ = (int) camLoc.z();

    if (!camOnDol) { // if camera is not on the dolphin
      // x,y,z bounds need to be within the range of + or - 2
      if (camX + 2 >= goX && camX - 2 <= goX) {
        if (camY + 2 >= goY && camY - 2 <= goY) {
          if (camZ + 2 >= goZ && camZ - 2 <= goZ) {
            collision = true;
            avHasObj = true;
          }
        }
      }
    }
    return collision;
  }

  public GameObject getAvatar() {
    return dol;
  }

  public Camera getCamera() {
    return cam;
  }

  public boolean isCamOnAv() {
    return camOnDol;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    super.keyPressed(e);
  }
}
