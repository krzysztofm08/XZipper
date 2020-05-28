
package xzipper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;
import javax.swing.border.Border;

public class XZipper extends JFrame
{
    public XZipper()
    {
           this.setTitle("XZipper");
           this.setBounds(300, 200, 300, 250);
           this.setJMenuBar(menuBarMain);
           
           JMenu fileMenu = menuBarMain.add(new JMenu("File"));
           
           Action addingAction = new ActionButtons("Add", "Add a file", "ctrl A", new ImageIcon("addIcon.jpg"));
           Action removingAction = new ActionButtons("Remove", "Remove a file", "ctrl R", new ImageIcon("removeIcon.jpg"));
           Action creatingNoteAction = new ActionButtons ("Create a note", "Create a .txt note", "ctrl N", new ImageIcon("noteIcon.jpg"));
           Action zippingAction = new ActionButtons ("ZIP", "ZIP files", "ctrl R", new ImageIcon("boxIcon.jpg"));
           Action exitAction = new ActionButtons ("Exit", "Exit the program", "ctrl T", new ImageIcon("exitIcon.jpg"));
           
           Action innerSaveAction = new ActionButtons ("Save a note", "Save a .txt note", "ctrl S", new ImageIcon("noteIcon.jpg"));
           
           JMenuItem menuAdd = fileMenu.add(addingAction);
           JMenuItem menuRemove = fileMenu.add(removingAction);
           JMenuItem menuCreateANote = fileMenu.add(creatingNoteAction);
           JMenuItem menuZip = fileMenu.add(zippingAction);
           JMenuItem menuExit = fileMenu.add(exitAction);
                      
           buttonAdd = new JButton(addingAction);
           buttonRemove = new JButton (removingAction);
           buttonZip = new JButton(zippingAction);
           buttonNote = new JButton(creatingNoteAction);
           
           innerSaveButton = new JButton(innerSaveAction);
           
           
           ActionListener timeCheck = new Clock();
           Timer clock = new Timer(1000,timeCheck);
           clock.start();    
           
           JScrollPane scroll = new JScrollPane(list);
           list.setBorder(BorderFactory.createEtchedBorder());

           GroupLayout layout = new GroupLayout(this.getContentPane());
           layout.setAutoCreateContainerGaps(true);
           layout.setAutoCreateGaps(true);
           
         layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addComponent(scroll, 100, 150, Short.MAX_VALUE)
                .addContainerGap(30, 30)
                .addGroup(
                layout.createParallelGroup().addComponent(buttonAdd, 130, 130, 130).addComponent(buttonRemove, 130, 130, 130).addComponent(buttonNote, 130, 130, 130).addComponent(buttonZip, 130, 130, 130).addComponent(timeLabel)
                )
                );
        
        layout.setVerticalGroup(
                layout.createParallelGroup()
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(
                layout.createSequentialGroup().addComponent(buttonAdd).addComponent(buttonRemove).addComponent(buttonNote).addGap(20, 50, Short.MAX_VALUE).addComponent(buttonZip).addComponent(timeLabel))
                );
                

           this.getContentPane().setLayout(layout);
           this.setDefaultCloseOperation(3);
    }
    
    private DefaultListModel listModel = new DefaultListModel()
    {
        @Override
        public void addElement(Object obj)
        {
            list.add(obj);
            super.addElement(((File)obj).getName());
        }
        @Override
        public Object get(int index)
        {
            return list.get(index);
        }
        @Override
        public Object remove(int index)
        {
            list.remove(index);
            return super.remove(index);
        }
        
        ArrayList list = new ArrayList();
    };
    
    private JList list = new JList(listModel);
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JButton buttonZip;
    private JButton buttonNote;
    private JMenuBar menuBarMain = new JMenuBar();
    private JButton innerSaveButton;
    
    
    private JLabel timeLabel = new JLabel(timer());
    private JFileChooser chooser = new JFileChooser();
    private JFileChooser innerChooser = new JFileChooser();
    
    JTextArea txtArea = new JTextArea(8, 15);
    

    public static void main(String[] args) 
    {
        new XZipper().setVisible(true);
    }
    
    public String timer()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        String h = ""+calendar.get(Calendar.HOUR_OF_DAY);
        String min = ""+calendar.get(Calendar.MINUTE);
        String sec = ""+calendar.get(Calendar.SECOND);
        
        if (Integer.parseInt(h) < 10)
            h = "0"+h;
        if (Integer.parseInt(min) < 10)
            min = "0"+min;
        if (Integer.parseInt(sec) < 10)
            sec = "0"+sec;
        
        return h + " : " + min + " : " + sec;
    }
    
    private class Clock implements ActionListener{

        public void actionPerformed(ActionEvent e)
        {
        timeLabel.setText(timer());
        }
    }
    
    private class ActionButtons extends AbstractAction
    {
        public ActionButtons (String actionName, String actionDescription, String actionShortcut)
        {
            this.putValue(NAME, actionName);
            this.putValue(SHORT_DESCRIPTION, actionDescription);
            this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(actionShortcut));
        }
        
        public ActionButtons (String actionName, String actionDescription, String actionShortcut, Icon icon)
        {
            this(actionName, actionDescription, actionShortcut);
            this.putValue(Action.SMALL_ICON, icon);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
         if (e.getActionCommand().equals("Add"))
                addFilesToArchive();
         if (e.getActionCommand().equals("Remove"))
                removeFilesFromArchive();
         if (e.getActionCommand().equals("Create a note"))
                createTxtNote();
         if (e.getActionCommand().equals("ZIP"))
                zippingAction();
         if (e.getActionCommand().equals("Exit"))
                closeApplication();
         if (e.getActionCommand().equals("Save a note"))
                innerSaveAction();
               
        }
        
        private void addFilesToArchive()
        {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            
            int tmp = chooser.showDialog(rootPane, "Add to archive");
            
            if ( tmp == JFileChooser.APPROVE_OPTION)
            {
                File[] paths = chooser.getSelectedFiles();
                
                for ( int i=0; i<paths.length; i++)
                {
                    if (!isItTheSame(paths[i].getPath()))
                        listModel.addElement(paths[i]);
                }
            }
        }
        
        private boolean isItTheSame(String testString)
        {
            for (int i=0; i<listModel.getSize(); i++)
            {
                if(((File)listModel.get(i)).getPath().equals(testString))
                    return true;
            }
                    return false;
        }
        
        private void removeFilesFromArchive()
        {
            int[] tmp = list.getSelectedIndices();
            for (int i=0; i<tmp.length; i++)
                listModel.remove(tmp[i]-i);
        }
        
        private void closeApplication()
        {
            System.exit(0);
        }
        
        private void createTxtNote()
        {
            new createTxtNote().setVisible(true);
        }
        
        private void innerSaveAction()
        {
            innerChooser.setSelectedFile(new File(System.getProperty("user.dir")+File.separator+"mynote.txt"));
            innerChooser.setMultiSelectionEnabled(false);
            
            innerChooser.showSaveDialog(rootPane);
            
            File tmpInner = innerChooser.getSelectedFile();
            
                try
                    {
                        PrintWriter savePrintWriter = new PrintWriter(new FileWriter(tmpInner));
            
                        for (String line : txtArea.getText().split("\\n")) savePrintWriter.println(line);
                        savePrintWriter.close(); 
                    }
            
                catch (IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
                
        }
    }
    
    public class createTxtNote extends JFrame
    {
        public createTxtNote()
         {
              JDesktopPane txtCreation = new JDesktopPane();
              this.setBounds(350, 350, 460, 200);
              this.setTitle("Create .txt note");
              this.setResizable(false);
              
              JScrollPane txtScroll = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
              
              GroupLayout innerLayout = new javax.swing.GroupLayout(getContentPane());
              getContentPane().setLayout(innerLayout);
              innerLayout.setHorizontalGroup(
              innerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(innerLayout.createSequentialGroup()
                .addContainerGap()
                  .addComponent(txtScroll, 300, 300, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(innerSaveButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(223, Short.MAX_VALUE))
               );
              innerLayout.setVerticalGroup(
              innerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(innerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(innerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(innerSaveButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtScroll, javax.swing.GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(210, Short.MAX_VALUE))
        );
              
         }
    }
    
    private void zippingAction()
        {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setSelectedFile(new File(System.getProperty("user.dir")+File.separator+"myname.zip"));
            int zippingTmp = chooser.showDialog(rootPane, "Zip File!");
            
            if (zippingTmp == JFileChooser.APPROVE_OPTION)
            {
               byte tmpData[] = new byte[BUFFOR];
               try
               {
                ZipOutputStream zOutS = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(chooser.getSelectedFile()),BUFFOR));

                for (int i = 0; i < listModel.getSize(); i++)
                {
                    if (!((File)listModel.get(i)).isDirectory())
                        zip(zOutS, (File)listModel.get(i), tmpData, ((File)listModel.get(i)).getPath());
                    else
                    {
                        writePaths((File)listModel.get(i));
                                
                        for (int j = 0; j < pathsList.size(); j++)
                          zip(zOutS, (File)pathsList.get(j), tmpData, ((File)listModel.get(i)).getPath());
                        
                        pathsList.removeAll(pathsList);
                    }
                        
                }

                zOutS.close();
               }
               catch(IOException e)
               {
                   System.out.println(e.getMessage());
               }
            }
        }
        private void zip(ZipOutputStream zOutS, File filePath, byte[] tmpData, String basePath) throws IOException
        {
            BufferedInputStream inS = new BufferedInputStream(new FileInputStream(filePath), BUFFOR);

            zOutS.putNextEntry(new ZipEntry(filePath.getPath().substring(basePath.lastIndexOf(File.separator)+1)));
            
            int counter;
            while ((counter = inS.read(tmpData, 0, BUFFOR)) != -1)
                zOutS.write(tmpData, 0, counter);


            zOutS.closeEntry();

            inS.close();            
        }
        public static final int BUFFOR = 1024;
       
        private void writePaths(File pathName)
        {
           String[] fileAndFolderNames = pathName.list();

           for (int i = 0; i < fileAndFolderNames.length; i++)
           {
               File p = new File(pathName.getPath(), fileAndFolderNames[i]);

               if (p.isFile())
                   pathsList.add(p);

               if (p.isDirectory())
                   writePaths(new File(p.getPath()));

           }
        }
        
        ArrayList pathsList = new ArrayList();
    
}
