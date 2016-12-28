package org.hy.common.redis;





/**
 * Redis���ݿ��������Ϣ
 * 
 * @author ZhengWei(HY)
 * @create 2014-09-17
 */
public class RData implements Cloneable
{
    
    /** ��Ҫ����Redis.Set�������չ���� */
    public enum PutType
    {
        /** �������͵ġ���� key �Ѿ���������ֵ�� SET �͸�д��ֵ���������� */
        Normal
        
        /** ֻ�ڼ�������ʱ���ŶԼ��������ò��� */
       ,NX
       
        /** ֻ�ڼ��Ѿ�����ʱ���ŶԼ��������ò��� */
       ,XX
    }
    
    
    private String   key;
    
    private String   field;
    
    private String   value;
    
    /** 
     * ���ü��Ĺ���ʱ��(��λ����)
     * 
     * 0����ʾ������
     */
    private int      expireTime;
    
    /**  */
    private PutType  putType;
    
    /** ʱ��� */
    private String   timestamp;
    
    
    
    public RData()
    {
        this(null ,null ,null);
    }
    
    
    
    public RData(String i_Key ,String i_Value)
    {
        this(i_Key ,null ,i_Value);
    }
    
    
    
    public RData(String i_Key ,String i_Field ,String i_Value)
    {
        this.key        = i_Key;
        this.field      = i_Field;
        this.value      = i_Value;
        this.expireTime = 0;
        this.putType    = PutType.Normal;
    }

    
    
    public String getKey()
    {
        return key;
    }

    
    
    public void setKey(String key)
    {
        this.key = key;
    }

    
    
    public String getField()
    {
        return field;
    }
    

    
    public void setField(String field)
    {
        this.field = field;
    }

    
    
    public String getValue()
    {
        return value;
    }

    
    
    public void setValue(String value)
    {
        this.value = value;
    }

    
    
    public int getExpireTime()
    {
        return expireTime;
    }


    
    public void setExpireTime(int expireTime)
    {
        this.expireTime = expireTime;
    }


    
    public PutType getPutType()
    {
        return putType;
    }



    
    public void setPutType(PutType putType)
    {
        this.putType = putType;
    }



    public String getTimestamp()
    {
        return timestamp;
    }


    
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }



    @Override
    protected RData clone()
    {
        RData v_Clone = new RData(this.key ,this.field ,this.value);
        
        v_Clone.setTimestamp( this.getTimestamp());
        v_Clone.setExpireTime(this.getExpireTime());
        
        return v_Clone;
    }
    
}
