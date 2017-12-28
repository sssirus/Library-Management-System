package com.qa.demo.dataStructure;
/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示实体及相关信息的数据结构；
 */

public class Entity {

    //表示实体的mention名字；
    private String mentionName;

    //表示实体URI;
    private String entityURI;

    //表示实体在KG中的名称;
    private String kgEntityName;

    //表示实体来源（百度百科、维基百科、互动百科、COIN自制KG）;
    private DataSource dataSource;

    public Entity()
    {

    }

    public Entity(Entity e)
    {
        this.setEntityURI(e.getEntityURI());
        this.setKgEntityName(e.getKgEntityName());
        this.setMentionName(e.getMentionName());
        this.setDataSource(e.getDataSource());
    }

    public String getEntityURI() {
        return entityURI;
    }

    public void setEntityURI(String entityURI) {
        this.entityURI = entityURI;
    }

    public String getMentionName() {
        return mentionName;
    }

    public void setMentionName(String mentionName) {
        this.mentionName = mentionName;
    }

    public String getKgEntityName() {
        return kgEntityName;
    }

    public void setKgEntityName(String kgEntityName) {
        this.kgEntityName = kgEntityName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int hashCode() {
        return entityURI.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof Entity) )
            return false;
        else {
            Entity e = (Entity) o;
            return e.entityURI.equals(entityURI);
        }
    }




}
