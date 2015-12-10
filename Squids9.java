int many=4;
Squid school[]=  new Squid[many];
String names[]=  { "Ottie", "Nono", "Deca", "Ariel"};
float spacing;

Boat Jimmy=  new Boat();

float surface;
float moonX=0, moonY=100;
int score=0;

//// SETUP:  size & reset.
void setup() {
  size(750,750);
  spacing=  width/(many+1);
  reset();
}
// Constuct squid(s).
void reset() {
  surface=  random(  height/4, height/2 );
  // Many squids.
  float x=  spacing;
  for (int i=0; i<many; i++ ) {
    school[i]=  new Squid( names[i], x );
    x += spacing;
  }
  Jimmy.name=  "Jimmy";
}


//// NEXT FRAME:  scene, action
void draw() {
  scene();
  if (key >= 'A' && key <= 'Z') {
    boatReport( 50, Jimmy, 1 );
    fishReport( surface+50, school, school.length);
  }
  else action();
  show();
  messages();
}
void messages() {
  fill(0);
  textSize( 20 );
  textSize(12);
   }

//// METHODS TO MOVE & DRAW.
void scene() {
  background( 50,150,200 );      // Dark sky.
  fill( 0,100,50 );
  noStroke();
  rect( 0,surface, width, height-surface );  
}
void action() {
  // Move squids & boats.
  for (int i=0; i<many; i++ ) {
    school[i].move();
  }
  Jimmy.move();
}
//// Display the squids in (sorted) order.
void show() {
  float x=  spacing;
  for (int i=0; i<many; i++ ) {
    school[i].x=  x;
    x += spacing;
    school[i].show();
  }
  Jimmy.show();
}

void boatReport( float top, Boat b, int many ) {
  fill(255,200,200);
  rect( 50,top, width-100, 50 + 20*many );
  float x=70, y=top+20;
 
  fill(0);
  //
  y += 15;
}
void fishReport( float top, Squid[] a, int many ) {
  fill(255,255,200);
  rect( 50,top, width-100, 50 + 20*many );
  float x=70, y=top+20;
  // Labels.
  for (int i=0; i<many; i++) {
    y += 15;    // Next line.
  }
}
    

//// EVENT HANDLERS:  keys, clicks ////
void keyPressed() {
  if (key == 'r') reset();
  // Send a squid to the bottom!
  if (key == '0') school[0].bottom(); 
  if (key == '1') school[1].bottom(); 
  if (key == '2') school[2].bottom(); 
  if (key == '3') school[3].bottom(); 
  //// Send highest to bottom.
  if (key == 'h') {
    int k=0;
    for (int i=1; i<many; i++ ) {
      if (school[i].y < school[k].y) k=i;           // k is index of highert.
    }
    school[k].bottom();     
  }
  // Cheat codes:
  //// Send all to top or bottom.
  if (key == 'b') {
    for (int k=0; k<many; k++ ) {
      school[k].bottom();     
    }
  }
  if (key == 't') {
    for (int k=0; k<many; k++ ) {
      school[k].y=  surface+10;
      school[k].dy=  -0.1  ;
    }
  }
}




//// OBJECTS ////

class Squid {
  float x,y;        // Coordinates
  float dx=0,dy=0;  // Speed
  float w=40,h=40;
  int legs=10;      // Number of legs.
  String name="";
  float r,g,b;      // Color.
  int count=0;
  //// CONSTRUCTORS ////
  Squid( String s, float x ) {
    this.name=  s;
    this.x=x;
    bottom();
    // Purplish
    r=  random(100, 255);
    g=  random(0, 100);
    b=  random(100, 250);
  }
  //// Start again at bottom.  (Random speed.)
  void bottom() {
    y=  height - h;
    dy=  -random( 0.1, 0.9 );
    legs=  int( random(1, 10.9) );
  }
  //// METHODS:  move & show ////
  void move() {
    x += dx;
    y += dy;
    if (y>height) { 
      bottom();
      count++;
    }
    else if (y<surface) { 
      dy=  -3 * dy;        // Descend fast!
    }
  }
  //// Display the creature.
  void show() {
    fill(r,g,b);
    stroke(r,g,b);
    ellipse( x,y, w,h );         // round top
    rect( x-w/2,y, w,h/2 );      // flat bottom
    fill(255);
    float blink=10;
    if ( y%100 > 80) blink=2;
    ellipse( x,y-h/4, 10, blink );     // eye
    // Legs
    fill(r,g,b);                 // legs.
    float legX=  x-w/2, foot=0;
    foot=  dy>=0 ? 0 : (y%47 > 23) ? 5 : -5;
    strokeWeight(3);
    for (int i=0; i<legs; i++) {
      line( legX, y+h/2, legX+foot, 20+y+h/2 );
      legX += w / (legs-1);
    }
      strokeWeight(3);
    fill(200,200,0);
    text( name, x-w/2, y-10+h/2 );
    fill(0);
    text( legs, x+2-w/2, y+h/2 );
    fill(255);
    if (count>0) text( count, x, y+h/2 );
  }
  //// Return true if near
  boolean hit( float xx, float yy ) {
    return dist( xx,yy, x,y ) < h;
  }
}


class Boat {
  String name="";
  float x=0, y=surface, dx=5;
  int cargo=0, caught=0;
  void move() {
    //// Fish before move:  check each squid.
    int caught=0;
    for (int i=0; i<many; i++ ) {
      if (school[i].hit( x, surface )) {
        caught += school[i].legs;
        school[i].bottom();
      }
    }
    cargo += caught;    
    //// Now, move the boat.
    x += dx;
    if (caught>0) x += 2*dx;      //  Jump after catch.
    if (x<0) {
      score += cargo;            // Add cargo to global score.
      cargo=0;
      dx = random( 1, 5 );      // Variable boat speed.
    }
    if (x>width)  {
      dx = -random( 0.5, 3 );    // Slower return.
    }
  }
  //// Draw the boat.
  void show() {
    // Boat.
    fill(255,0,0);
    noStroke();
    rect( x, surface-20, 50, 20 );
    if (dx>0)   triangle( x+50,surface, x+50,surface-20, x+70,surface-20 );
    else        triangle( x,surface, x,surface-20, x-20,surface-20 );
    rect( x+12, surface-30, 25, 10 );      // Cabin.
    fill(0);
    rect( x+20, surface-40, 10, 10 );      // Smokestack.
    // Display name & cargo.
    fill(255);
    text( name, x+5, surface-10 );
    fill(0);
    if (cargo>0) text( cargo, x+20, surface );
  }    
}
