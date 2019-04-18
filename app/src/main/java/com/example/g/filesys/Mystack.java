package com.example.g.filesys;

public class Mystack {
    static class mystack {
        int mytop;
        String stack[];

        public mystack(int num) {
            mytop = -1;
            stack = new String[num];
        }

        /*出栈*/
        void mypop() {
            mytop--;
        }
        void clear(int i){
            mytop = -1;
            stack = new String[i];
        }

        /*入栈*/
        void mypush(String x) {
            mytop++;
            stack[mytop] = x;
        }

        /*判空*/
        Boolean myisempty() {
            if (mytop == -1) return true;
            else return false;
        }

        /*取栈顶元素*/
       String mypeek() {
            return stack[mytop];
        }
        String mylast(){
           if (mysize()==0){
               return "/storage/emulated/0";
           }else {
               return stack[mytop - 1];
           }
        }
        /*栈大小*/
        int mysize() {
            return mytop + 1;
        }
    }
}
