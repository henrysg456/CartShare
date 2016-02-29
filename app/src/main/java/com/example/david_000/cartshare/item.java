package com.example.david_000.cartshare;

/**
 * Created by david_000 on 2/16/2016.
 */
public class item
{
    private String id;
    private String title;
    //private String content;

    item(String itemId, String itemTitle/*, String itemContent*/)
    {
        id = itemId;
        title = itemTitle;
        //content = itemContent;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    /*
   public String getContent() {
       return content;
   }
   public void setContent(String content) {
       this.content = content;
   }
*/
    @Override
    public String toString() {  return this.getTitle();  }

}  //end item class
