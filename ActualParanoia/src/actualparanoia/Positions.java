/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actualparanoia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author root
 */
class Positions {
    char[][] a;
    Positions() throws IOException {
        
        Path path = Paths.get(".");
        
        String command = path.toAbsolutePath().normalize().toString();
        //System.out.println(command+"/src/resources/");
        ProcessBuilder pb = new ProcessBuilder(command+"/src/resources/ProcessImg.m");
        
        
        try{
            Process p = pb.start();
            
            //p.waitFor();
            //Debug Code
            //pb.redirectErrorStream(true);
            /*BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;

            while((s=bf.readLine())!=null){
                System.out.println(s);
            }

            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();*/
        }
        catch(IOException ex){
            ex.printStackTrace();
        } 
        
        Path data_file = Paths.get(command+"/src/resources/data.txt");
        List<String> linewiseInfo=Files.readAllLines(data_file);
        char[][] matrixOfLocations = new char[linewiseInfo.size()][];
        int i=0;
        for(String s: linewiseInfo){
            s = s.replaceAll(",", "");
          matrixOfLocations[i] = s.toCharArray();
            
           i++;
        }
         a = matrixOfLocations;
    }
    
    
    void updateHit(int i,int j){
        a[i][j] = '0';
    }
    
    char[][] getPositions(){
        return a;
    }

    
}