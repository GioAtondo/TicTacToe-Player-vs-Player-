
package Servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author Giovanni Alberto Salas Atondo
 */
public class Servidor {
    //Iniciamos el puerto
    private final int puerto = 2027;
    //Numero maximo de conexiones
    private final int noConexiones = 2;
    //Creamos una lista de sockets para guardar el socket de cada jugador
    private LinkedList<Socket> usuarios = new LinkedList<Socket>();
    //Variable para controlar turno
    private Boolean turno = true;
    //matriz donde se guardan movimientos
    private int G[][] = new int[3][3];
    //Numero de veces que turnan
    private int turnos = 1;
    
    //Funcion para que el servidor empieze a recibir conecciones
    public void escuchar(){
        try{
            //Iniciamos la matriz del juego
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    G[i][j] = -1;
                }
            }
            //Creamos el socket servidor
            ServerSocket servidor = new ServerSocket(puerto,noConexiones);
            //Ciclo infinito para estar escuchando por nuevos jugadores
            System.out.println("Esperando jugadores...");
            while(true){
                //cuando un jugador se conecta guardamos el socket en nuestra lista
                Socket cliente = servidor.accept();
                //Se agrega el socket a la lista
                usuarios.add(cliente);
                //Se le genera un turno X o O
                int xo = turnos % 2 == 0 ? 1 : 0;
                turnos++;
                //Intanciamos un hilo que estara atendiendo al cliente 
                Runnable run = new HiloServidor(cliente,usuarios,xo,G);
                Thread hilo = new Thread(run);
                hilo.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Funcion main para correr el servidor
    public static void main(String[] args){
        Servidor servidor = new Servidor();
        servidor.escuchar();
    }
    
}
