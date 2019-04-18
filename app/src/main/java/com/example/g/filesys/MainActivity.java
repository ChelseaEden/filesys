package com.example.g.filesys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName() + "--->";
    private String rootPath;
    private String Path;
    private RecyclerView recyclerView;
    TextView textview;
    AlertDialog.Builder dialog;
    private List<Fileit> fileList;
    private List<Fileit> orderList;
    private List<Fileit> finalList;
    Mystack.mystack myStack = new Mystack.mystack(20);
    private boolean copymode = false;
    private boolean cutmode = false;
    private boolean isfile = false;
    private FloatingActionButton floatButton;
    final String items[] = {"复制", "剪切", "重命名", "删除","压缩"};
    String copyPath;
    String filename;
    FileUtil fileutil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initlist();
    }
    private void init(){
        //获取所需权限

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        //初始化控件
        fileutil = new FileUtil();
        textview = (TextView) findViewById(R.id.path);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        floatButton = (FloatingActionButton)findViewById(R.id.float_button);
        floatButton.setImageResource(R.drawable.pic_add);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件夹名称")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File file = new File("/storage/emulated/0");
                                Toast.makeText(MainActivity.this,"/storage/emulated/0",Toast.LENGTH_SHORT).show();
                                if (!file.exists()) {
                                    file.mkdirs();
                                }else{
                                    Toast.makeText(MainActivity.this,"文件夹已存在",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });
    }
    private void initlist(){
        rootPath = getRootPath();
        myStack.mypush(rootPath);
        getFileDir(rootPath);
    }
    private void getFileDir(final String filePath) {
        textview.setText(filePath);
        File file = new File(filePath);
        File[] files = file.listFiles();
        fileList = new ArrayList<>();
        if (files == null) {
            Toast.makeText(this, "请获取权限！", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < files.length; i++) {
            int j = 0;
            File f = files[i];
           if (f.isFile()){
               j = 1;
           }
            fileList.add(new Fileit(f.getName(),j,f.getPath()));
            //   }
        }
        orderList = orderByName(fileList);
        finalList = new ArrayList<>();
        if (!filePath.equals(rootPath)){
            finalList.add(new Fileit("返回根目录",2,rootPath));
            finalList.add(new Fileit("返回上一层",3,myStack.mylast()));
        }else{
            myStack = new Mystack.mystack(20);
            myStack.mypush(rootPath);
        }
        getlist(orderList,finalList);
        FileAdapter adapter = new FileAdapter(this,finalList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Fileit s = finalList.get(postion);
                Path = s.getPath();
                if(s.getImageId()!=3)
                {
                    myStack.mypush(Path);
                }
                if (s.getImageId()==1){                                 //判断点击item是否为文件
                   new Openfile(getApplicationContext(),s.getPath());
                }else {
                    if(s.getImageId()==3){
                        myStack.mypop();
                    }
                        getFileDir(Path);
                }
            }
        });
        adapter.setOnItemLongClickListener(new FileAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                final Fileit s = finalList.get(postion);
                Log.d(TAG, "长按了"+s.getName());
                Path = s.getPath();
                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(s.getName())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(items[which].equals("复制")) {
                                    File file = new File(s.getPath());
                                    if (file.isFile()){
                                        isfile = true;
                                        copymode = true;
                                        copyPath = Path;
                                        filename = s.getName();
                                        floatButton.setImageResource(R.drawable.pic_paste);
                                    }
                                    if (file.isDirectory()){
                                        isfile =false;
                                        copymode = true;
                                        copyPath = Path;
                                        floatButton.setImageResource(R.drawable.pic_paste);
                                    }
                                    Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                                }
                                if(items[which].equals("剪切")) {
                                    File file = new File(s.getPath());
                                    if (file.isFile()){
                                        isfile = true;
                                        cutmode = true;
                                        copyPath = Path;
                                        filename = s.getName();
                                        floatButton.setImageResource(R.drawable.pic_paste);
                                    }
                                    if (file.isDirectory()){
                                        isfile =false;
                                        cutmode = true;
                                        copyPath = Path;
                                        floatButton.setImageResource(R.drawable.pic_paste);
                                    }
                                    Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                                }
                                if(items[which].equals("重命名")) {
                                    final EditText et = new EditText(MainActivity.this);
                                    new AlertDialog.Builder(MainActivity.this).setTitle("请输入更改的名称").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (!et.getText().toString().equals(s.getName())) {
                                                fileutil.renameFile(filePath,s.getName(),et.getText().toString());
                                                getFileDir(filePath);
                                            } else {
                                                Toast.makeText(MainActivity.this, "文件名相同", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).setNegativeButton("取消", null).show();
                                }
                                if(items[which].equals("删除")) {
                                    fileutil.delete(s.getPath());
                                    getFileDir(filePath);
                                }
                                if(items[which].equals("压缩")) {
                                    final EditText et = new EditText(MainActivity.this);
                                    new AlertDialog.Builder(MainActivity.this).setTitle("请输入压缩文件名").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            FileOutputStream fos1 = null;
                                            try {
                                                fos1 = new FileOutputStream(filePath+"/"+et.getText().toString());
                                                fileutil.toZip(s.getPath(),fos1,true);
                                                getFileDir(filePath);

                                            } catch (FileNotFoundException e) {
                                                Toast.makeText(MainActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }).setNegativeButton("取消", null).show();

                                }
                            }
                        });
                dialog.show();
            }
        });
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!copymode && !cutmode) {
                    final EditText et = new EditText(MainActivity.this);
                    new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件夹名称").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File file = new File(filePath + "/" + et.getText());
                            Toast.makeText(MainActivity.this, filePath + "/" + et.getText(), Toast.LENGTH_SHORT).show();
                            if (!file.exists()) {
                                file.mkdirs();
                                getFileDir(filePath);
                            } else {
                                Toast.makeText(MainActivity.this, "文件夹已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
                }
                else if(cutmode){
                    if (isfile) {
                        copyFile(copyPath, filePath);
                        fileutil.delete(copyPath);
                        cutmode = false;
                        floatButton.setImageResource(R.drawable.pic_add);
                        getFileDir(filePath);
                    }else{
                        copyFolder(copyPath, filePath);
                        fileutil.delete(copyPath);
                        cutmode = false;
                        floatButton.setImageResource(R.drawable.pic_add);
                        getFileDir(filePath);
                    }
                }
                else{
                    if (isfile) {
                        copyFile(copyPath, filePath);
                        copymode = false;
                        floatButton.setImageResource(R.drawable.pic_add);
                        getFileDir(filePath);
                    }else{
                        copyFolder(copyPath, filePath);
                        copymode = false;
                        floatButton.setImageResource(R.drawable.pic_add);
                        getFileDir(filePath);
                    }
            }
        }
        });
    }


    public boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name+"/"+filename);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e("--Method--", "copyFolder: cannot create directory.");
                    return false;
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e("--Method--", "copyFolder:  oldFile not exist.");
                    return false;
                } else if (!temp.isFile()) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.");
                    return false;
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.");
                    return false;
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String getRootPath(){

        try {
            String rootPath;
                Log.d(TAG, "getRootPath: 正在获取内置SD卡根目录");
                rootPath = Environment.getExternalStorageDirectory()
                        .toString();
                Log.d(TAG, "getRootPath: 内置SD卡目录为:" + rootPath);
                return rootPath;
        } catch (Exception e) {
            return null;
        }
    }
    public static List<Fileit> orderByName(List<Fileit> filelist) {
        List<Fileit> FileNameList = new ArrayList<>();
        Collections.sort(filelist, new Comparator<Fileit>() {
            @Override
            public int compare(Fileit o1, Fileit o2) {
                if (o1.getImageId()==0 && o2.getImageId()==1)
                    return -1;
                if (o1.getImageId()==1 && o2.getImageId()==0)
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Fileit file1 : filelist) {
                FileNameList.add(new Fileit(file1.getName(),file1.getImageId(),file1.getPath()));
        }
        return FileNameList;
    }
    public void getlist(List<Fileit> list ,List<Fileit> flist) {
        for (Fileit file2 : list) {
            flist.add(new Fileit(file2.getName(),file2.getImageId(),file2.getPath()));
        }
    }
}
