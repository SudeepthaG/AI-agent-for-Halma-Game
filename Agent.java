
import java.awt.Point;
import java.io.*;
import static java.lang.Integer.*;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;

public class HalmaAgent {
   
    public static void main(String[] args)throws Exception 
    { 
		//reading input
	File input = new File("input.txt"); 
	FileWriter output=new FileWriter("output.txt");
	Scanner sc=new Scanner(input);
	String move=sc.nextLine();
	String player=sc.nextLine();
	float timeleft=Float.valueOf(sc.nextLine());
	String[] p=new String[16];
	char[][] pos=new char[16][16];
	for(int i=0;i<16;i++)
	{
		p[i]=sc.nextLine();
		pos[i]=p[i].toCharArray();
	}	
	System.out.println(move+"\n"+"player"+player+"\n"+timeleft+" \nboard");
        for(int i=0;i<16;i++)
        {    
            for(int j=0;j<16;j++)
                System.out.print(pos[i][j]);
            System.out.println();
        }
        Solution s=new Solution(pos,move,player,timeleft);
        SingleSolution ss=new SingleSolution(pos,move,player,timeleft);
        String path="";
        File file=new File("playdata.txt");
        if(!file.exists())
        {
            FileWriter playdata=new FileWriter("output.txt"); 
            playdata.write("0");
        }
        if(move.equals("SINGLE"))
            path=ss.solutionMainFunction(3);
        else
            path=path+s.solutionMainFunction(3); 
            //varying depth based on time left as greater the depth, greater the time taken to generate a move
      if(path.equals(""))
            {
                if(timeleft>270)
                    path=path+s.solutionMainFunction(2);
                else if(timeleft<270&&timeleft>150)
                    path=path+s.solutionMainFunction(3);
                else if(timeleft<150&&timeleft>50)
                    path=path+s.solutionMainFunction(3);
                else if(timeleft>30)
                    path=path+s.solutionMainFunction(2);
                else if(timeleft>20)
                    path=path+s.solutionMainFunction(2);
                else
                    path=path+s.solutionMainFunction(1);
            }
        }
        System.out.println(path);
	output.write(path);
	output.close();
	//input.close();
    }
}


class Solution
{
    char board[][]=new char[16][16];
    String move;
    String player;
    float time;
    int basepos[]=new int[2];
    char p;
    Point bestmovesrc=new Point();
    Point bestmovedest=new Point();
	
    Solution(char board[][],String move,String player,float timeleft)
    {
        this.board=board;
        this.move=move;
        this.player=player;
        this.time=timeleft;
        if(player.equals("WHITE"))
            p='W';
        else
            p='B';
    }
        
    String solutionMainFunction(int depth)
    {
        String path;
        alphabetasearch(board,depth);
         if(bestmovesrc.equals(bestmovedest))
            dummymoves(board,p);
        int srcx=(int)bestmovesrc.getX();
        int srcy=(int)bestmovesrc.getY();
        int destx=(int)bestmovedest.getX();
        int desty=(int)bestmovedest.getY();
        if(abs(srcx-destx)==1||abs(srcy-desty)==1)
            path="E "+srcy+","+srcx+" "+desty+","+destx+"\n";
        else
                path=findJumpPath(srcx,srcy,destx,desty);
        return path;
    }
    
    String findJumpPath(int srcx,int srcy,int finaldestx,int finaldesty)
    {
        String path="";
        int destx=finaldestx;
        int desty=finaldesty;
        if(board[srcx][srcy]==p)
            System.out.println("\n\ntrue\n\npath:"+srcy+","+srcx+" to "+desty+","+destx);
        else
            System.out.println("\n\nfalse\n\n");
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++)
                System.out.print(board[i][j]);
            System.out.println();
        }           
        LinkedList<Point> jumps=new LinkedList<>();
        jumps.addAll(calculateJumpMove(srcx,srcy,board,jumps,p));
	    //checking all possible jump moves
        int jumpx[]={-2,-2,2,2,-2,0,2,0};
        int jumpy[]={-2,2,-2,2,0,2,0,-2}; 
        int midx[]={-1,-1,1,1,-1,0,1,0};
        int midy[]={-1,1,-1,1,0,1,0,-1};
        int counter=0;
        LinkedList<Point> visited=new LinkedList<>();
        LinkedList<Point> visitedtemp=new LinkedList<>();
        LinkedList<Point> visitedAll=new LinkedList<>();
        HashMap<Point, List> moves = new HashMap<>();
        List<String> paths=new LinkedList<>();
        for(int m=0;m<8;m++)
        {
            if(srcx+jumpx[m]>=0&&srcy+jumpy[m]>=0&&srcx+jumpx[m]<16&&srcy+jumpy[m]<16&&board[srcx+jumpx[m]][srcy+jumpy[m]]=='.'&&(board[srcx+midx[m]][srcy+midy[m]]=='W'||board[srcx+midx[m]][srcy+midy[m]]=='B'))
            {
                visited.add(new Point(srcx+jumpx[m],srcy+jumpy[m]));
                paths.add("J "+srcy+","+srcx+" "+(srcy+jumpy[m])+","+(srcx+jumpx[m])+" ");
                if(finaldestx==srcx+jumpx[m]&&finaldesty==srcy+jumpy[m])
                {
                    return "J "+srcy+","+srcx+" "+finaldesty+","+finaldestx;
                }
            }
        }
        moves.put(new Point(srcx,srcy), visited);
        visitedtemp.addAll(visited);\
        visited.clear();
        int i;
        while(visitedtemp.size()>0)
        {
            Point newsrc=visitedtemp.get(0);
            visitedtemp.remove(0);
            visitedAll.add(newsrc);
            int newsrcx=(int)newsrc.getX();
            int newsrcy=(int)newsrc.getY();
            
            for(int m=0;m<8;m++)
            {
                if(newsrcx+jumpx[m]>=0&&newsrcy+jumpy[m]>=0&&newsrcx+jumpx[m]<16&&newsrcy+jumpy[m]<16&&board[newsrcx+jumpx[m]][newsrcy+jumpy[m]]=='.'&&(board[newsrcx+midx[m]][newsrcy+midy[m]]=='W'||board[newsrcx+midx[m]][newsrcy+midy[m]]=='B'))
                {
                    if(!visitedAll.contains(new Point(newsrcx+jumpx[m],newsrcy+jumpy[m])))
                    {
                        
                        visitedAll.add(new Point(newsrcx+jumpx[m],newsrcy+jumpy[m]));
                        visited.add(new Point(newsrcx+jumpx[m],newsrcy+jumpy[m]));
                        paths.add("J "+newsrcy+","+newsrcx+" "+(newsrcy+jumpy[m])+","+(newsrcx+jumpx[m])+" ");
                        if(finaldestx==newsrcx+jumpx[m]&&finaldesty==newsrcy+jumpy[m])
                        {
                            counter=1;
                        }
                    }    
                }
            }
            moves.put(newsrc, visited);
            visitedtemp.addAll(visited);
            visited.clear();
        }
            System.out.println(paths);
            String t=""+finaldesty+","+finaldestx;
            for(int j=paths.size()-1;j>=0;j--)
            {
                
                if(paths.get(j).split(" ")[2].equals(t))
                    {
                        path=path+paths.get(j);
                        break;
                    }
            }
            System.out.println(path);
            for(int j=0;j<paths.size()-1;j++)
            {
                String temp=path.split(" ")[1];
                for(int k=0;k<paths.size();k++)
                {
                    if(paths.get(k).split(" ")[2].equals(temp))
                    {
                        path=paths.get(k)+"\n"+path;
                        break;
                    }
                }
                    
            }
        System.out.println("path before returnin:"+path);   
        return path;    
    }
	
    void dummymoves(char[][] currentboard,char currentp)
    {
        int movex[]={-1,-1,-1,0,0,1,1,1};
        int movey[]={-1,0,1,-1,1,-1,0,1};
        int m,counter=0;
        for(int i=15;i>=0;i--)
        {
            for(int j=15;j>=0;j--)
            {
                if(currentboard[i][j]==currentp)
                {
                    for(m=0;m<8;m++) 
                    {
                        if(i+movex[m]>=0&&j+movey[m]>=0&&i+movex[m]<16&&j+movey[m]<16&&currentboard[i+movex[m]][j+movey[m]]=='.')
                        {
                            bestmovesrc=new Point(i,j);
                            bestmovedest=new Point(i+movex[m],j+movey[m]);
                            counter=1;
                            break;
                        }
                    }    
               }
               if(counter==1)
                   break;
            }
            if(counter==1)
                break;
        }
        System.out.println("dummy moves updated");
    }
    
    void alphabetasearch(char[][] currentboard,int depth)
    {
        int value=maximize(currentboard,depth,-10000,10000);   
    }
    
    boolean isWinning(char[][] currentboard,char currentp)          //returns true if opposite base camp is completely occupied for winning
    {
        if(currentp=='W')
            if(currentboard[0][0]=='W'&&currentboard[0][1]=='W'&&currentboard[0][2]=='W'&&currentboard[0][3]=='W'&&currentboard[0][4]=='W'&&currentboard[1][0]=='W'&&currentboard[1][1]=='W'&&currentboard[1][2]=='W'&&currentboard[1][3]=='W'&&currentboard[1][4]=='W'&&currentboard[2][0]=='W'&&currentboard[2][1]=='W'&&currentboard[2][2]=='W'&&currentboard[2][3]=='W'&&currentboard[3][0]=='W'&&currentboard[3][1]=='W'&&currentboard[3][2]=='W'&&currentboard[4][0]=='W'&&currentboard[4][1]=='W')
                return true;
            else
                return false;
        else
            if(currentboard[15][15]=='B'&&currentboard[15][14]=='B'&&currentboard[15][13]=='B'&&currentboard[15][12]=='B'&&currentboard[15][11]=='B'&&currentboard[14][15]=='B'&&currentboard[14][14]=='B'&&currentboard[14][13]=='B'&&currentboard[14][12]=='B'&&currentboard[14][11]=='B'&&currentboard[13][15]=='B'&&currentboard[13][14]=='B'&&currentboard[13][13]=='B'&&currentboard[13][12]=='B'&&currentboard[12][15]=='B'&&currentboard[12][14]=='B'&&currentboard[12][13]=='B'&&currentboard[11][15]=='B'&&currentboard[11][14]=='B')
                return true;
            else
                return false;           
    }
    
    boolean isBaseEmpty(char[][] currentboard,char currentp)        //returns true if base camp is emptied
    {
        if(currentp=='B')
            if(currentboard[0][0]=='B'||currentboard[0][1]=='B'||currentboard[0][2]=='B'||currentboard[0][3]=='B'||currentboard[0][4]=='B'||currentboard[1][0]=='B'||currentboard[1][1]=='B'||currentboard[1][2]=='B'||currentboard[1][3]=='B'||currentboard[1][4]=='B'||currentboard[2][0]=='B'||currentboard[2][1]=='B'||currentboard[2][2]=='B'||currentboard[2][3]=='B'||currentboard[3][0]=='B'||currentboard[3][1]=='B'||currentboard[3][2]=='B'||currentboard[4][0]=='B'||currentboard[4][1]=='B')
                return false;
            else
                return true;
        else
            if(currentboard[15][15]=='W'||currentboard[15][14]=='W'||currentboard[15][13]=='W'||currentboard[15][12]=='W'||currentboard[15][11]=='W'||currentboard[14][15]=='W'||currentboard[14][14]=='W'||currentboard[14][13]=='W'||currentboard[14][12]=='W'||currentboard[14][11]=='W'||currentboard[13][15]=='W'||currentboard[13][14]=='W'||currentboard[13][13]=='W'||currentboard[13][12]=='W'||currentboard[12][15]=='W'||currentboard[12][14]=='W'||currentboard[12][13]=='W'||currentboard[11][15]=='W'||currentboard[11][14]=='W')
                return false;
            else
                return true;           
    }
    
    HashMap moveGenerator(char[][] currentBoard,char currentp)
    {
        HashMap<Point, List> moves = new HashMap<>();
        
        for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                List<Point> ll=new LinkedList<>();
                LinkedList<Point> jumps=new LinkedList<>();
                Point src=new Point(i,j);
                if(currentBoard[i][j]==currentp)
                {
                    ll.addAll(calculateEdgeMove(i,j,currentBoard,currentp));
                    jumps.addAll(calculateJumpMove(i,j,currentBoard,jumps,currentp));
                      
                }
                ll.addAll(jumps);
                moves.put(src,ll);
            }
        }
        return moves; 
    }
	
    List calculateEdgeMove(int i,int j,char[][] currentBoard,char currentp)
    {
        int movex[]={-1,-1,-1,0,0,1,1,1};
        int movey[]={-1,0,1,-1,1,-1,0,1};
        int m;
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        Point a=new Point(i,j);
        List<Point> ll=new LinkedList<>();
        for(m=0;m<8;m++)
        {
            if(i+movex[m]>=0&&j+movey[m]>=0&&i+movex[m]<16&&j+movey[m]<16&&currentBoard[i+movex[m]][j+movey[m]]=='.')
            {   
                if(currentp=='B'&&!campA.contains(a)&&campA.contains(new Point(i+movex[m],j+movey[m])))
                {
			if(((i>=(i+movex[m]))&&(j>=(j+movey[m])))||!campA.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }  
                else if(currentp=='W'&&campB.contains(new Point(i,j)))
               {
                    if(((i>=(i+movex[m]))&&(j>=(j+movey[m])))||!campB.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
               else if(currentp=='B'&&campA.contains(new Point(i,j)))
                {
                    if(((i<=i+movex[m])&&(j<=movey[m]))&&!campA.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
                else if(currentp=='B'&&campB.contains(new Point(i,j)))
                {
                   if(campB.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }     
                }
                else if(currentp=='W'&&campA.contains(new Point(i,j)))
                {
                    if(campA.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
                
                else
                {
                    a=new Point(i+movex[m],j+movey[m]);
                    if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                }    
            }        
            
        } 
        return ll;
    }
	
    List calculateJumpMove(int i,int j,char[][] currentBoard,LinkedList ll,char currentp)
    {
        int jumpx[]={-2,-2,2,2,-2,0,2,0};
        int jumpy[]={-2,2,-2,2,0,2,0,-2}; 
        int midx[]={-1,-1,1,1,-1,0,1,0};
        int midy[]={-1,1,-1,1,0,1,0,-1};
        Point a=new Point(i,j);
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        List<Point> templl=new LinkedList<>();
        for(int m=0;m<8;m++)
        {
            if(i+jumpx[m]>=0&&j+jumpy[m]>=0&&i+jumpx[m]<16&&j+jumpy[m]<16&&currentBoard[i+jumpx[m]][j+jumpy[m]]=='.'&&(currentBoard[i+midx[m]][j+midy[m]]=='W'||currentBoard[i+midx[m]][j+midy[m]]=='B'))
            {
                if(currentp=='W'&&campB.contains(new Point(i,j)))
                {
                    if(((i>=(i+jumpx[m]))&&(j>=(j+jumpy[m])))||!campB.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                    }
                }
                else if(currentp=='B'&&campA.contains(new Point(i,j)))
                {
                    if(((i<=i+jumpx[m])&&(j<=jumpy[m]))||!campA.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                    }
                }
                else if(currentp=='B'&&campB.contains(new Point(i,j)))
                {
                   if(campB.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                    }     
                }
                else if(currentp=='W'&&campA.contains(new Point(i,j)))
                {
                    if(campA.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                    }
                }
                
                else
                {
                    
                    a=new Point(i+jumpx[m],j+jumpy[m]);
                    if(!ll.contains(a))
                        {
                            ll.add(a);
                        }
                } 
            }
        }
        Set<Point> lls=new HashSet<>(ll);
        ll.clear();
        ll=new LinkedList<>(lls);
        templl.addAll(ll);
        
        while(templl.size()>0)
        {
           Point p=templl.get(0);
           templl.remove(0);
           i=(int)p.getX();
           j=(int)p.getY();
           for(int m=0;m<8;m++)
            {
                if(i+jumpx[m]>=0&&j+jumpy[m]>=0&&i+jumpx[m]<16&&j+jumpy[m]<16&&currentBoard[i+jumpx[m]][j+jumpy[m]]=='.'&&(currentBoard[i+midx[m]][j+midy[m]]=='W'||currentBoard[i+midx[m]][j+midy[m]]=='B'))
                {
                    if(currentp=='W'&&campB.contains(new Point(i,j)))
                    {
                        if(((i>=(i+jumpx[m]))&&(j>=(j+jumpy[m])))||!campB.contains(new Point(i+jumpx[m],j+jumpy[m])))
                        {
                            a=new Point(i+jumpx[m],j+jumpy[m]);
                            if(!ll.contains(a))
                            {
                                ll.add(a);
                                templl.add(a);
                            }
                        }
                    }
                    else if(currentp=='B'&&campB.contains(new Point(i,j)))
                    {
                       if(campB.contains(new Point(i+jumpx[m],j+jumpy[m])))
                        {
                            a=new Point(i+jumpx[m],j+jumpy[m]);
                            if(!ll.contains(a))
                            {
                                templl.add(a);
                                ll.add(a);
                            }
                        }     
                    }
                    else if(currentp=='W'&&campA.contains(new Point(i,j)))
                    {
                        if(campA.contains(new Point(i+jumpx[m],j+jumpy[m])))
                        {
                            a=new Point(i+jumpx[m],j+jumpy[m]);
                            if(!ll.contains(a))
                            {
                                templl.add(a);
                                ll.add(a);
                            }
                        }
                    }
                    else if(currentp=='B'&&campA.contains(new Point(i,j)))
                    {
                        if(((i<=i+jumpx[m])&&(j<=jumpy[m]))||!campA.contains(new Point(i+jumpx[m],j+jumpy[m])))
                        {
                            a=new Point(i+jumpx[m],j+jumpy[m]);
                            if(!ll.contains(a))
                            {
                                templl.add(a);
                                ll.add(a);
                            }
                        }
                    }
                    else
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                        {
                            templl.add(a);
                            ll.add(a);
                        }
                    } 
                } 
            }
        }
         return ll;
    }
    
    char[][] move(Point src,Point newpos,char currentp,char[][] currentboard)
    {
        currentboard[(int)src.getX()][(int)src.getY()]='.';
        currentboard[(int)newpos.getX()][(int)newpos.getY()]=currentp; 
        return currentboard;
    }
	
    char[][] undomove(Point src,Point newpos,char currentp,char[][] currentboard)
    {

        currentboard[(int)src.getX()][(int)src.getY()]='.';
        currentboard[(int)newpos.getX()][(int)newpos.getY()]=currentp; 
        return currentboard;
    }
    
    public int maximize(char currentboard[][],int depth,int alpha,int beta)
    {
        char currentp=p;
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        
        Point bestsrc=new Point();
        Point bestdest=new Point();
        int evalvalue;
        
        if(depth==0||isWinning(currentboard,currentp))
        {
            evalvalue=evaluate(currentboard,currentp);
            return evalvalue;            //return int in function by returning util value..store the moves in boardaftermove
        }
        HashMap<Point, List> moves = new HashMap<>();
        List<Point> ll=new LinkedList<>(); 
        int best=-10000;
        moves=moveGenerator(currentboard,currentp);   
        for(Map.Entry<Point,List> entry : moves.entrySet())    
        {       
            
            Point src=new Point(entry.getKey());
            ll=entry.getValue();
            if(!isBaseEmpty(currentboard,currentp))
                {
                    if(currentp=='W')
                        if(!campB.contains(src))
                            continue;
                    if(currentp=='B')
                        if(!campA.contains(src))
                            continue;
                }
            for(Point pos:ll) 
            {
                if(src.equals(pos))
                    continue;
                char[][] newboard=move(src,pos,currentp,currentboard);
                
                evalvalue=minimize(newboard,depth-1,alpha,beta);
                currentboard=undomove(pos,src,currentp,currentboard);
                
                if(evalvalue>best)
                {
                    best=evalvalue;
                    bestmovesrc=src;
                    bestmovedest=pos;
                }
                if(best>=beta)
                    return best;
                alpha=max(best,alpha);
            }
        } 
        return best; 
    }
    
    public int minimize(char currentboard[][],int depth,int alpha,int beta)
    {
        char currentp;
        int evalvalue;
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));      
        if(p=='W')
            currentp='B';
        else
            currentp='W';
        Point bestsrc=new Point();
        Point bestdest=new Point();
        if(depth==0||isWinning(currentboard,currentp))
        {    
            evalvalue=evaluate(currentboard,currentp);
            return evalvalue;    
        }
        HashMap<Point, List> moves = new HashMap<>();
        List<Point> ll=new LinkedList<>(); 
        int best=10000;
        moves=moveGenerator(currentboard,currentp);   
        for(Map.Entry<Point,List> entry : moves.entrySet())    
        {       
            Point src=new Point(entry.getKey());
            ll=entry.getValue();
            if(!isBaseEmpty(currentboard,currentp))
                {
                    if(currentp=='W')
                        if(!campB.contains(src))
                            continue;
                    if(currentp=='B')
                        if(!campA.contains(src))
                            continue;
                }
            for(Point pos:ll) 
            {
                if(src.equals(pos))
                    continue;
                char[][] newboard=move(src,pos,currentp,currentboard);
                evalvalue=maximize(newboard,depth-1,alpha,beta);
                currentboard=undomove(pos,src,currentp,currentboard);
                if(evalvalue<best)
                {
                    best=evalvalue;
                }
                if(best<=alpha)
                    return best;
                beta=min(best,beta);
            }
        }
        return best;
    }
    
    int evaluate(char[][] currentboard,char currentp)
    {
        int value=0;
        Point base;
        if(isWinning(currentboard,currentp))
            return 10000;
        if(p=='W')
        {
            base=new Point(15,15);
            if(!isBaseEmpty(currentboard,currentp))
                return 0;
            if(isWinning(currentboard,currentp))
                return 10000;
        }    
        else
        {
            base=new Point(0,0);
            if(!isBaseEmpty(currentboard,currentp))
                return 0;
            if(isWinning(currentboard,currentp))
                return 10000;
        }  
        int val[][] = {{160,159,158,157,156,150,140,130,120,110,100,90,80,70,50,40},{159,158,157,156,155,150,140,130,120,110,100,90,80,70,50,40},{157,156,155,154,150,140,130,120,110,100,90,80,70,50,40,30},{156,155,154,150,140,130,120,110,100,90,80,70,50,40,30,20},{155,154,113,112,111,110,109,108,107,106,105,104,103,102,101,100},{105,104,103,102,101,100,99,98,97,96,95,94,93,92,91,90},{95,94,93,92,91,90,89,88,87,86,85,84,83,82,81,80},{85,84,83,82,81,80,79,78,77,76,75,74,73,72,71,70},{75,74,73,72,71,70,69,68,67,66,65,64,63,62,61,60},{65,64,63,62,61,60,59,58,57,56,55,54,53,52,51,50},{55,54,53,52,51,50,49,48,47,46,45,44,43,42,41,40},{54,53,52,51,50,49,48,47,46,45,44,43,42,41,31,30},{43,42,41,40,39,38,37,36,35,34,33,32,31,22,21,20},{32,31,30,29,28 ,27,26,25,24,23,22,21,13,12,11,10},{21,20,19,18,17,16,15,14,13,12,11,9,8,7,6,5},{11,10,9,8,7,6,5,4,3,2,1,0,0,0,0,0}};
        if(currentp=='W')
            for(int i=0;i<16;i++)
            {
                for(int j=0;j<16;j++)
                {

                    if(currentboard[i][j]==currentp)
                        value=value+val[i][j];      
                }   
            }
        else
            for(int i=15;i>=0;i--)
            {
                for(int j=15;j>=0;j--)
                {
                        if(currentboard[i][j]==currentp)
                        value=value+val[i][j];
                }
            } 
        return value;
    }
    
}

class SingleSolution
{
    char board[][]=new char[16][16];
    String move;
    String player;
    float time;
    int basepos[]=new int[2];
    char p;
    Point bestmovesrc=new Point();
    Point bestmovedest=new Point();
    SingleSolution(char board[][],String move,String player,float timeleft)
    {
        this.board=board;
        this.move=move;
        this.player=player;
        this.time=timeleft;
        if(player.equals("WHITE"))
            p='W';
        else
            p='B';
    }
    
    String solutionMainFunction(int depth)
    {
        String path;
        alphabetasearch(board,depth);
         if(bestmovesrc.equals(bestmovedest))
            dummymoves(board,p);
        int srcx=(int)bestmovesrc.getX();
        int srcy=(int)bestmovesrc.getY();
        int destx=(int)bestmovedest.getX();
        int desty=(int)bestmovedest.getY();
        if(abs(srcx-destx)==1||abs(srcy-desty)==1)
            path="E "+srcy+","+srcx+" "+desty+","+destx+"\n";
        else
             path="J "+srcy+","+srcx+" "+desty+","+destx+"\n";
        char[][] newboardx=move(bestmovesrc,bestmovedest,p,board);
        return path;
    }
    
    void dummymoves(char[][] currentboard,char currentp)
    {
        int movex[]={-1,-1,-1,0,0,1,1,1};
        int movey[]={-1,0,1,-1,1,-1,0,1};
        int m,counter=0;
        for(int i=15;i>=0;i--)
        {
            for(int j=15;j>=0;j--)
            {
                if(currentboard[i][j]==currentp)
                {
                    for(m=0;m<8;m++) 
                    {
                        if(i+movex[m]>=0&&j+movey[m]>=0&&i+movex[m]<16&&j+movey[m]<16&&currentboard[i+movex[m]][j+movey[m]]=='.')
                        {
                            bestmovesrc=new Point(i,j);
                            bestmovedest=new Point(i+movex[m],j+movey[m]);
                            counter=1;
                            break;
                        }
                    }    
               }
               if(counter==1)
                   break;
            }
            if(counter==1)
                break;
        }
    }
    
    void alphabetasearch(char[][] currentboard,int depth)
    {
        int value=maximize(currentboard,depth,-10000,10000);   
    }
    
    boolean isWinning(char[][] currentboard,char currentp)          //returns true if opposite base is completely occupied for winning
    {
        if(currentp=='W')
            if(currentboard[0][0]=='W'&&currentboard[0][1]=='W'&&currentboard[0][2]=='W'&&currentboard[0][3]=='W'&&currentboard[0][4]=='W'&&currentboard[1][0]=='W'&&currentboard[1][1]=='W'&&currentboard[1][2]=='W'&&currentboard[1][3]=='W'&&currentboard[1][4]=='W'&&currentboard[2][0]=='W'&&currentboard[2][1]=='W'&&currentboard[2][2]=='W'&&currentboard[2][3]=='W'&&currentboard[3][0]=='W'&&currentboard[3][1]=='W'&&currentboard[3][2]=='W'&&currentboard[4][0]=='W'&&currentboard[4][1]=='W')
                return true;
            else
                return false;
        else
            if(currentboard[15][15]=='B'&&currentboard[15][14]=='B'&&currentboard[15][13]=='B'&&currentboard[15][12]=='B'&&currentboard[15][11]=='B'&&currentboard[14][15]=='B'&&currentboard[14][14]=='B'&&currentboard[14][13]=='B'&&currentboard[14][12]=='B'&&currentboard[14][11]=='B'&&currentboard[13][15]=='B'&&currentboard[13][14]=='B'&&currentboard[13][13]=='B'&&currentboard[13][12]=='B'&&currentboard[12][15]=='B'&&currentboard[12][14]=='B'&&currentboard[12][13]=='B'&&currentboard[11][15]=='B'&&currentboard[11][14]=='B')
                return true;
            else
                return false;           
    }
    
    boolean isBaseEmpty(char[][] currentboard,char currentp)        //returns true if base is emptied
    {
        if(currentp=='B')
            if(currentboard[0][0]=='B'||currentboard[0][1]=='B'||currentboard[0][2]=='B'||currentboard[0][3]=='B'||currentboard[0][4]=='B'||currentboard[1][0]=='B'||currentboard[1][1]=='B'||currentboard[1][2]=='B'||currentboard[1][3]=='B'||currentboard[1][4]=='B'||currentboard[2][0]=='B'||currentboard[2][1]=='B'||currentboard[2][2]=='B'||currentboard[2][3]=='B'||currentboard[3][0]=='B'||currentboard[3][1]=='B'||currentboard[3][2]=='B'||currentboard[4][0]=='B'||currentboard[4][1]=='B')
                return false;
            else
                return true;
        else
            if(currentboard[15][15]=='W'||currentboard[15][14]=='W'||currentboard[15][13]=='W'||currentboard[15][12]=='W'||currentboard[15][11]=='W'||currentboard[14][15]=='W'||currentboard[14][14]=='W'||currentboard[14][13]=='W'||currentboard[14][12]=='W'||currentboard[14][11]=='W'||currentboard[13][15]=='W'||currentboard[13][14]=='W'||currentboard[13][13]=='W'||currentboard[13][12]=='W'||currentboard[12][15]=='W'||currentboard[12][14]=='W'||currentboard[12][13]=='W'||currentboard[11][15]=='W'||currentboard[11][14]=='W')
                return false;
            else
                return true;           
    }
    
    HashMap moveGenerator(char[][] currentBoard,char currentp)
    {
        HashMap<Point, List> moves = new HashMap<>();
        
        for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                List<Point> ll=new LinkedList<>();
                LinkedList<Point> jumps=new LinkedList<>();
                List<Point> tjumps=new LinkedList<>();
                Point src=new Point(i,j);
                if(currentBoard[i][j]==currentp)
                {
                    ll.addAll(calculateEdgeMove(i,j,currentBoard,currentp));
		    jumps.addAll(calculateJumpMove(i,j,currentBoard,jumps,currentp));   
                }
                ll.addAll(jumps);
                moves.put(src,ll);
            }
        }
        return moves; 
    }
	
    List calculateEdgeMove(int i,int j,char[][] currentBoard,char currentp)
    {
        int movex[]={-1,-1,-1,0,0,1,1,1};
        int movey[]={-1,0,1,-1,1,-1,0,1};
        int m;
         List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        Point a=new Point(i,j);
        List<Point> ll=new LinkedList<>();
        for(m=0;m<8;m++)
        {
            if(i+movex[m]>=0&&j+movey[m]>=0&&i+movex[m]<16&&j+movey[m]<16&&currentBoard[i+movex[m]][j+movey[m]]=='.')
            {
               if(currentp=='W'&&((i==15&&j==15)||(i==15&&j==14)||(i==15&&j==13)||(i==15&&j==12)||(i==15&&j==11)||(i==14&&j==15)||(i==14&&j==14)||(i==14&&j==13)||(i==14&&j==12)||(i==14&&j==11)||(i==13&&j==15)||(i==13&&j==14)||(i==13&&j==13)||(i==13&&j==12)||(i==12&&j==15)||(i==12&&j==14)||(i==12&&j==13)||(i==11&&j==15)||(i==11&&j==14)))
                {
                    if((i>=(i+movex[m]))&&(j>=(j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
                else if(currentp=='B'&&campB.contains(new Point(i,j)))
                {
                   if(campB.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }     
                }
                else if(currentp=='W'&&campA.contains(new Point(i,j)))
                {
                    if(campA.contains(new Point(i+movex[m],j+movey[m])))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
                else if(currentp=='B'&&campA.contains(new Point(i,j)))
                {
                    if((i<=i+movex[m])&&(j<=movey[m]))
                    {
                        a=new Point(i+movex[m],j+movey[m]);
                        ll.add(a);
                    }
                }
                else
                {
                    a=new Point(i+movex[m],j+movey[m]);
                    ll.add(a);
                }    
            }        
        } 
        return ll;
    }
	
    List calculateJumpMove(int i,int j,char[][] currentBoard,LinkedList ll,char currentp)
    {
        int jumpx[]={-2,-2,2,2,-2,0,2,0};
        int jumpy[]={-2,2,-2,2,0,2,0,-2}; 
        int midx[]={-1,-1,1,1,-1,0,1,0};
        int midy[]={-1,1,-1,1,0,1,0,-1};
        Point a=new Point(i,j);
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));     
        List<Point> templl=new LinkedList<>();
        for(int m=0;m<8;m++)
        {
            if(i+jumpx[m]>=0&&j+jumpy[m]>=0&&i+jumpx[m]<16&&j+jumpy[m]<16&&currentBoard[i+jumpx[m]][j+jumpy[m]]=='.'&&(currentBoard[i+midx[m]][j+midy[m]]=='W'||currentBoard[i+midx[m]][j+midy[m]]=='B'))
            {
                if(currentp=='W'&&((i==15&&j==15)||(i==15&&j==14)||(i==15&&j==13)||(i==15&&j==12)||(i==15&&j==11)||(i==14&&j==15)||(i==14&&j==14)||(i==14&&j==13)||(i==14&&j==12)||(i==14&&j==11)||(i==13&&j==15)||(i==13&&j==14)||(i==13&&j==13)||(i==13&&j==12)||(i==12&&j==15)||(i==12&&j==14)||(i==12&&j==13)||(i==11&&j==15)||(i==11&&j==14)))
                {
                    if((i>=(i+jumpx[m]))&&(j>=(j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                            ll.add(a);
                    }
                }
                else if(currentp=='B'&&campB.contains(new Point(i,j)))
                {
                   if(campB.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                            ll.add(a);
                    }     
                }
                else if(currentp=='W'&&campA.contains(new Point(i,j)))
                {
                    if(campA.contains(new Point(i+jumpx[m],j+jumpy[m])))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                            ll.add(a);
                    }
                }
                else if(currentp=='B'&&campA.contains(new Point(i,j)))
                {
                    if((i<=i+jumpx[m])&&(j<=jumpy[m]))
                    {
                        a=new Point(i+jumpx[m],j+jumpy[m]);
                        if(!ll.contains(a))
                            ll.add(a);
                    }
                }
                else
                {
                    a=new Point(i+jumpx[m],j+jumpy[m]);
                    if(!ll.contains(a))
                        ll.add(a);
                } 
            }
            return ll;
    }
    
    char[][] move(Point src,Point newpos,char currentp,char[][] currentboard)
    {
        return currentboard;
    }
    char[][] undomove(Point src,Point newpos,char currentp,char[][] currentboard)
    {
        currentboard[(int)src.getX()][(int)src.getY()]='.';
        currentboard[(int)newpos.getX()][(int)newpos.getY()]=currentp; 
        return currentboard;
    }
    
    public int maximize(char currentboard[][],int depth,int alpha,int beta)
    {
        BoardAfterMove b;
        char currentp=p;
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        
        Point bestsrc=new Point();
        Point bestdest=new Point();
        int evalvalue;
        if(depth==0||isWinning(currentboard,currentp))
        {
            evalvalue=evaluate(currentboard,currentp);
            return evalvalue;          //return int in fction by returning util value.
        }
        HashMap<Point, List> moves = new HashMap<>();
        List<Point> ll=new LinkedList<>(); 
        int best=-10000;
        moves=moveGenerator(currentboard,currentp);   
        for(Map.Entry<Point,List> entry : moves.entrySet())    
        {       
            
            Point src=new Point(entry.getKey());
            ll=entry.getValue();
            if(!isBaseEmpty(currentboard,currentp))
                {
                    if(currentp=='W')
                        if(!campB.contains(src))
                            continue;
                    if(currentp=='B')
                        if(!campA.contains(src))
                            continue;
                }
            for(Point pos:ll) 
            {
                if(src.equals(pos))
                    continue;
                char[][] newboard=move(src,pos,currentp,currentboard);
                
                evalvalue=minimize(newboard,depth-1,alpha,beta);
                currentboard=undomove(pos,src,currentp,currentboard);
                if(evalvalue>best)
                {
                    best=evalvalue;
                    bestmovesrc=src;
                    bestmovedest=pos;
                }
                if(best>=beta)
                    return best;
                alpha=max(best,alpha);
            }
        } 
        return best;
    }
    
    public int minimize(char currentboard[][],int depth,int alpha,int beta)
    {
        char currentp;
        int evalvalue;
        BoardAfterMove b;
        List<Point> campA=Arrays.asList(new Point(0,0),new Point(0,1),new Point(0,2),new Point(0,3),new Point(0,4),new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(2,0),new Point(2,1),new Point(2,2),new Point(2,3),new Point(3,0),new Point(3,1),new Point(3,2),new Point(4,0),new Point(4,1));
        List<Point> campB=Arrays.asList(new Point(15,15),new Point(15,14),new Point(15,13),new Point(15,12),new Point(15,11),new Point(14,15),new Point(14,14),new Point(14,13),new Point(14,12),new Point(14,11),new Point(13,15),new Point(13,14),new Point(13,13),new Point(13,12),new Point(12,15),new Point(12,14),new Point(12,13),new Point(11,15),new Point(11,14));
        if(p=='W')
            currentp='B';
        else
            currentp='W';
        Point bestsrc=new Point();
        Point bestdest=new Point();
        if(depth==0||isWinning(currentboard,currentp))
        {    
            evalvalue=evaluate(currentboard,currentp);
            return evalvalue;    
        }
        HashMap<Point, List> moves = new HashMap<>();
        List<Point> ll=new LinkedList<>(); 
        int best=10000;
        moves=moveGenerator(currentboard,currentp);   
        for(Map.Entry<Point,List> entry : moves.entrySet())    
        {       
            Point src=new Point(entry.getKey());
            ll=entry.getValue();
            if(!isBaseEmpty(currentboard,currentp))
                {
                    if(currentp=='W')
                        if(!campB.contains(src))
                            continue;
                    if(currentp=='B')
                        if(!campA.contains(src))
                            continue;
                }
            for(Point pos:ll) 
            {
                if(src.equals(pos))
                    continue;
                char[][] newboard=move(src,pos,currentp,currentboard);
                evalvalue=maximize(newboard,depth-1,alpha,beta);
                currentboard=undomove(pos,src,currentp,currentboard);
                if(evalvalue<best)
                {
                    best=evalvalue;
                }
                if(best<=alpha)
                    return best;
                beta=min(best,beta);
            }
        }
        return best;
    }
    
    int evaluate(char[][] currentboard,char currentp)
    {
        int value=0;
        Point base;
        if(isWinning(currentboard,currentp))
            return 1000;
        if(p=='W')
        {
            base=new Point(15,15);
            if(!isBaseEmpty(currentboard,currentp))
                return 0;
            if(isWinning(currentboard,currentp))
                return 1000;
        }    
        else
        {
            base=new Point(0,0);
            if(!isBaseEmpty(currentboard,currentp))
                return 0;
            if(isWinning(currentboard,currentp))
                return 1000;
        }    
        for(int i=0;i<16;i++)
        {
            for(int j=0;j<16;j++)
            {
                
                if(currentboard[i][j]==currentp)
                    value=(int)(value+base.distance(i,j));      
            }   
        }
        return value;
    }   
}
