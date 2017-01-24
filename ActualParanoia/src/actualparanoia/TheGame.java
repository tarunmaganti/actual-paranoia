/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actualparanoia;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author root
 */
public class TheGame extends JFrame{
    
    private static final long serialVersionUID = 5663760293144882635L;
	
    public static final double SCALE = 45.0;
	
    public static final double NANO_TO_BASE = 1.0e9;
    
    
    //For Listeners
    private Point point;
    
    private Positions positions = null;
    
    private Queue<GameObject> removeHit;
    
    protected GameObject oBall, oSlider;
    protected Canvas canvas;
    protected World world;	
    protected boolean stopped;
    protected long last;
    
    private Vector2 sliderAcceleration;
    
    private boolean flag = false;
    
    public static class GameObject extends Body {
        
        protected Color color;
        
        public GameObject() {
            
            this.color = new Color(
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);
        }

        
        public void render(Graphics2D g) {
            
            AffineTransform ot = g.getTransform();
            AffineTransform lt = new AffineTransform();
            lt.translate((this.transform.getTranslationX() * SCALE),(this.transform.getTranslationY() * SCALE));
            lt.rotate(this.transform.getRotation());
            g.transform(lt);

            this.fixtures.stream().map((fixture) -> fixture.getShape()).forEachOrdered((convex) -> {
                Graphics2DRenderer.render(g, convex, SCALE, color);
            });

            // set the original transform
            g.setTransform(ot);
        }
    }
  
    
    private final class BounceListener extends CollisionAdapter{
        private Body b1,b2;
        
        public BounceListener(Body a, Body b){
            b1 = a;
            b2 = b;
        }
        
        @Override
        public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
			
			if ((body1 == b1) && (body2 == b2) ||(body1 == b2)&&(body2 == b1)){
				/*body1.getLinearVelocity().zero();
				body1.setAngularVelocity(0.0);
				body2.getLinearVelocity().zero();
				body2.setAngularVelocity(0.0);*/
                                
                                //Case 2
                               /* body2.applyForce(body1.getForce().getNegative());
                                body2.getLinearVelocity().negate().multiply(1.0);
                                */
                                Vector2 velocityBall = b1.getLinearVelocity(),
                                        velocitySliderX = b2.getLinearVelocity().getXComponent();
                                
                                
                                Vector2 newVelocityBall = new Vector2(velocityBall.add(velocitySliderX.multiply(0.2)));
                                
                                b1.setLinearVelocity(newVelocityBall.negate().multiply(1.01));
                                //b1.applyForce(new Vector2(0.5,20));
                                   
				return false;
			}
			return true;
		}
        
    }
    
    private final class BounceListenerBlock extends CollisionAdapter{
        private Body b1,b2;
        
        public BounceListenerBlock(Body a, Body b){
            b1 = a;
            b2 = b;
        }
        
        @Override
        public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
			
			if ((body1 == b1) && (body2 == b2)) {
                                removeHit.add((GameObject)body2);
                            body1.setLinearVelocity(new Vector2(body1.getLinearVelocity().getXComponent().negate(),body1.getLinearVelocity().getYComponent().negate()));
                            
                            return false;
			}
			return true;
		}
        
    }
    
    private final class BoundaryCollision extends CollisionAdapter{
        
        private Body b1,b2,floor;
        
        BoundaryCollision(GameObject a,GameObject b, GameObject floo){
            b1=a;
            b2=b;
            floor = floo;
        }
      
        @Override
        public boolean collision(Body body1, BodyFixture bf, Body body2, BodyFixture bf1, Penetration pntrtn) {
            if((b1 == body1) && (b2 == body2)){
                
                if(b2.equals(floor)){
                    flag = true;
                    
                    JOptionPane.showMessageDialog(new JFrame(), null,"Boo!", 0); 
                    return false;
                }
                
                body1.setLinearVelocity(body1.getLinearVelocity().negate());
                return false;
            }
            return true; 
       }
        
    }
    
    private final class SliderListener implements KeyListener {
     
        //private Body b1,b2;
        public SliderListener(){  
        }

        @Override
        public void keyTyped(KeyEvent ke) {
            //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            double x = 1.2;
            if(ke.getKeyCode() ==KeyEvent.VK_LEFT){
                
                sliderAcceleration = new Vector2(-x,0.0);
            }
            else if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
                sliderAcceleration = new Vector2(x,0.0);
            } 
        }

        @Override
        public void keyReleased(KeyEvent ke) {
            sliderAcceleration = new Vector2(0.0,0.0);
        }
        
       
    }
    
    
    TheGame(){
        super("The Paranoia");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                        stop();
                        super.windowClosing(e);
                }
        });
        Dimension size = new Dimension(800, 650);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);
        this.canvas.setFocusable(true);
        
        KeyListener kl = new SliderListener();
        this.canvas.addKeyListener(kl);
        
        
        this.add(this.canvas);

	this.setResizable(true);
        this.pack();
        this.stopped = false;
        this.initializeWorld();
    }
    
    protected final void initializeWorld() {
        this.world = new World();
        this.world.setGravity(new Vector2(0.0,-7.0));
        flag = false;
        //Boundaries
        Rectangle boundaryShape = new Rectangle(20,0.01);
        
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(boundaryShape));
        floor.translate(0,-5.2);
        
        GameObject leftBoundary = new GameObject();
        leftBoundary.addFixture(new BodyFixture(boundaryShape));
        leftBoundary.rotate(Math.toRadians(90));
        leftBoundary.translate(-8.85,0);
        
        GameObject rightBoundary = new GameObject();
        rightBoundary.addFixture(new BodyFixture(boundaryShape));
        rightBoundary.rotate(Math.toRadians(90));
        rightBoundary.translate(+8.85,0);
        
        
        GameObject top = new GameObject();
        top.addFixture(new BodyFixture(boundaryShape));
        top.translate(0,+7.65);
        
        
        
        this.world.addBody(floor);
        this.world.addBody(leftBoundary);
        this.world.addBody(rightBoundary);
        this.world.addBody(top);
        
    
        //Slider
        Rectangle sliderShape = new Rectangle(4,0.2);
        GameObject slider = new GameObject();
        slider.addFixture(new BodyFixture(sliderShape));
        slider.setMass(MassType.NORMAL);
        slider.translate(0,-5);
        sliderAcceleration = new Vector2(0.0,0.0);
        slider.setLinearVelocity(sliderAcceleration);
        
        this.world.addBody(slider);
        oSlider = slider;
        
        //Circle
        Circle paraBallShape = new Circle(0.2);
        GameObject paraBall = new GameObject();
        paraBall.addFixture(new BodyFixture(paraBallShape));
        paraBall.setMass(MassType.NORMAL);
        paraBall.translate(0,4);
        //paraBall.applyForce(new Vector2(0,-20.0));
        paraBall.setLinearVelocity(new Vector2(-0.09,-7.0));
        paraBall.setLinearDamping(0);
        paraBall.setAngularDamping(0.0);
        
        oBall = paraBall;
        
        this.world.addBody(paraBall);
        
        this.world.addListener(new BounceListener(paraBall,slider));
            
        this.world.addListener(new BoundaryCollision(paraBall,leftBoundary,floor));
        this.world.addListener(new BoundaryCollision(paraBall,rightBoundary,floor));
        this.world.addListener(new BoundaryCollision(paraBall,top,floor));
        this.world.addListener(new BoundaryCollision(paraBall,floor,floor));
        
        removeHit = new LinkedList();
        
        initializeBlocks(paraBall);
        
    }
    
    protected void initializeBlocks(GameObject paraBall){
        
        
        try {
            positions = new Positions();
        } catch (IOException ex) {
            Logger.getLogger(TheGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        char positionArray[][] = positions.getPositions();
  
        double g = 0.85,f = 0.4;
        Rectangle blockShape = new Rectangle(g,f);
        
        for(int i=0;i<20;i++)
        {
            for(int j=0;j<20;j++){
                GameObject block =new GameObject();
                block.addFixture(new BodyFixture(blockShape));
                block.setMass(MassType.INFINITE);
                block.translate(-8.4+(g*i),-1+(f*j));
                if(positionArray[i][j]=='1'){
              
                    this.world.addBody(block);
                    this.world.addListener(new BounceListenerBlock(paraBall,block));
                }
            }
        }
        
    }
    
    public void start(){
        this.last = System.nanoTime();
        
        this.canvas.setIgnoreRepaint(true);
        
        this.canvas.createBufferStrategy(2);
        
        Thread t = new Thread(){
            @Override
            public void run(){
                while(!stopped){
                    gameloop();
                }
            }
        };
        
        t.setDaemon(true);
        
        t.start();
        
    }
    
    protected void gameloop(){
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();
        
        
        AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
        AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
        g.transform(yFlip);
        g.transform(move);
        this.render(g);
        
        
        
        
        g.dispose();
        
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        if(!strategy.contentsLost()){
            strategy.show();
        }
        
        Toolkit.getDefaultToolkit().sync();
        
        long time = System.nanoTime();
        long diff = time - this.last;
        this.last = time;
    	double elapsedTime = diff / NANO_TO_BASE;
        this.world.update(elapsedTime);
    }
    
    protected void render(Graphics2D g){
        g.setColor(Color.WHITE);
        g.fillRect(-400, -300, 800, 600);

        g.translate(0.0, -1.0 * SCALE);

        this.oSlider.applyForce(sliderAcceleration);
        
        if(flag){
            initializeWorld();
        }
        
        Iterator<GameObject> it = removeHit.iterator();
        while(it.hasNext()){
            this.world.removeBody(it.next());
        }
        
        removeHit.clear();
        
        for (int i = 0; i < this.world.getBodyCount(); i++) {
                GameObject go = (GameObject) this.world.getBody(i);
                
                go.render(g);
        }
    }
    
    public synchronized void stop(){
        this.stopped = true;
    }
    
    public synchronized boolean isStopped() {
        return this.stopped;
    }


    
}