package client.command;

import alpha.FileManager;
import alpha.exception.ErrorCode;
import alpha.id.StringId;
import client.fmlayer.FMClient;
import client.fmlayer.FileImpl;

public class DeleteFileCmd extends Command {
    public DeleteFileCmd(String[] cmds) {
        if(cmds.length != 3) {
            throw new ErrorCode(7);
        } else {
            try {
                deleteFile(cmds[1], cmds[2]);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void deleteFile(String fmStringId, String filename) {
        FileManager fm = null;

        //System.out.println(fmStringId + ", " + filename);
        try{
            fm = new FMClient(new StringId(fmStringId));
            System.out.println(fm.deleteFile(new StringId(filename)));

            FileImpl localFile;
            // file is already opened, delete the local file
            if(openedFileSet.containsKey(filename)) {
                openedFileSet.remove(filename);
            }

        } catch (ErrorCode e) {
            throw new ErrorCode(e.getErrorCode());
        } catch (Exception e) {
            throw new ErrorCode(1000);
        }
    }

}
