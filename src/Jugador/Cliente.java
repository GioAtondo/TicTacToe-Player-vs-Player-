
package Jugador;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author Giovanni Alberto Salas Atondo
 */
public class Cliente implements Runnable{
    //Variables que ocupamos para la conexion
    private Socket cliente;
    private DataOutputStream out;
    private DataInputStream in;
    //Puerto
    private int puerto = 2027;
    //Si estamos en una sola maquina se utilizara localhost en cambio escribimos
    //un ip
    private String host = "192.168.1.69";
    
    //Variables para el frame
    private String mensaje;
    private Main frame;
    private JButton[][] botones;
    private ActionListener ac;
    
    //Variables para cargar las imagenes (X y 0)
    private Image X;
    private Image O;
    
    private boolean turno;
    
    //Constructor que va recibir como parametro la ventana, para poder hacer modificaciones
    //en botones
    public Cliente(Main frame){
        try{
            this.frame = frame;
            //Cargar imagenes
            X = ImageIO.read(getClass().getResource("X.png"));
            O = ImageIO.read(getClass().getResource("O.png"));
            //Creamos el socket con el host y el puerto
            //Declaramos los streams de comunicacion
            cliente = new Socket(host,puerto);
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
            //Tomamos una matriz con los 9 botones
            botones = this.frame.getBotones();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void run() {
        try{
            //Cuando conectamos con el servidor, este nos devuelve el turno 
            mensaje = in.readUTF();
            String split[] = mensaje.split(";");
            frame.cambioTexto(split[0]);
            String XO = split[0].split(" ")[1];
            turno = Boolean.valueOf(split[1]);
            
            //Ciclo infinito, para escuchar movimientos de jugadores
            while(true){
                //Recibir mensaje
                mensaje = in.readUTF();
                /*
                El mensaje esta compuesto por una cadena separada por ; cada separacion representa un dato
                    mensaje[0] : representa X o O 
                    mensaje[1] : representa fila del tablero
                    mensaje[2] : representa columna del tablero
                    mensaje[3] : representa estado del juego [Perdiste, Ganaste, Empate]
                */
                
                String[] mensajes = mensaje.split(";");
                int xo = Integer.parseInt(mensajes[0]);
                int f = Integer.parseInt(mensajes[1]);
                int c = Integer.parseInt(mensajes[2]);
                
                //Modificamos el boton presionado 
                if(xo == 1){
                    botones[f][c].setIcon(new ImageIcon(X));
                }else{
                    botones[f][c].setIcon(new ImageIcon(O));
                }
                //Bloquear el click al boton para que no se vuelva a presionar
                botones[f][c].removeActionListener(botones[f][c].getActionListeners()[0]);
                turno = !turno;
                
                //Dependiendo de la matriz de mensajes[3] mostraremos si ganas,
                //pierdes o enmpatas
                if(XO.equals(mensajes[3])){
                    JOptionPane.showMessageDialog(frame, "GANASTEE!");
                    new Main().setVisible(true);
                    frame.dispose();
                }
                else if("EMPATE".equals(mensajes[3])){
                    JOptionPane.showMessageDialog(frame, "EMPATE");
                    new Main().setVisible(true);
                    frame.dispose();
                }
                else if(!"NADIE".equals(mensajes[3]) && !mensajes[3].equals(mensajes[0])){
                    JOptionPane.showMessageDialog(frame, "PERDISTE BUU!");
                    new Main().setVisible(true);
                    frame.dispose();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Metodo que sirve para enviar la jugada al servidor
    public void enviarTurno(int f,int c){
        //Comprobamos que sea nuestro turno sino devolemos un mensaje
        try{
            if(turno){
                String datos = "";
                datos += f + ";";
                datos += c + ";";
                out.writeUTF(datos);
            }
            else{
                JOptionPane.showMessageDialog(frame,"Espera tu turno");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
