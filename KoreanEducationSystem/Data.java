package morph;

class Data implements Comparable<Data>
{
   double dis;
   String data;
   
   public Data(double dis, String data)
   {
      this.dis = dis;
      this.data = data;
   }
   
   public int compareTo(Data d)
   {
      if(this.dis > d.dis)
      {
         return 1;
      }
      else if(this.dis < d.dis)
      {
         return -1;
      }
      else
      {
         return 0;
      }
   }
}