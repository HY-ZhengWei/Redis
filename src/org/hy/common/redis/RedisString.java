package org.hy.common.redis;

import java.util.List;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Transaction;

import org.hy.common.JavaHelp;





/**
 * Redis���ݿ��е�String�������ز�����
 * 
 * @author ZhengWei(HY)
 * @create 2014-10-15
 */
public class RedisString
{
    private final static String [] $PutType_NXXX   = {"" ,"NX" ,"XX"};
    
    /** EX|PX, expire time units: EX = seconds; PX = milliseconds */
    private final static String    $ExpireTimeType = "EX";
    
    
    private Redis redis;
    
    
    
    public RedisString(Redis i_Redis)
    {
        if ( i_Redis == null )
        {
            throw new NullPointerException("Redis is null.");
        }
        
        this.redis = i_Redis;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��GET key
     * 
     * ���� key ���������ַ���ֵ��
     * ��� key ��������ô��������ֵ nil ��
     * ���� key �����ֵ�����ַ������ͣ�����null����Ϊ GET ֻ�����ڴ����ַ���ֵ��
     * 
     * @param i_Key
     * @return
     */
    public String get(String i_Key)
    {
        return this.core_get(i_Key);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��GET key
     * 
     * ���� key ���������ַ���ֵ��
     * ��� key ��������ô��������ֵ nil ��
     * ���� key �����ֵ�����ַ������ͣ�����null����Ϊ GET ֻ�����ڴ����ַ���ֵ��
     * 
     * @param i_Key
     * @return
     */
    private String core_get(String i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        String       v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.get(i_Key);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��STRLEN key
     * 
     * ���� key ��������ַ���ֵ�ĳ��ȡ�
     * �� key ����Ĳ����ַ���ֵʱ������null��
     * 
     * @param i_Key
     * @return
     */
    public Long length(String i_Key)
    {
        return this.core_StringLen(i_Key);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��STRLEN key
     * 
     * ���� key ��������ַ���ֵ�ĳ��ȡ�
     * �� key ����Ĳ����ַ���ֵʱ������null��
     * 
     * @param i_Key
     * @return
     */
    private Long core_StringLen(String i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        Long         v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.strlen(i_Key);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��GETRANGE key start end
     *
     * ���� key ���ַ���ֵ�����ַ������ַ����Ľ�ȡ��Χ�� start �� end ����ƫ��������(���� start �� end ����)��
     * ����ƫ������ʾ���ַ������ʼ������ -1 ��ʾ���һ���ַ��� -2 ��ʾ�����ڶ������Դ����ơ�
     * GETRANGE ͨ����֤���ַ�����ֵ��(range)������ʵ���ַ�����ֵ������������Χ��ֵ������
     * 
     * @param i_Key
     * @param i_Start
     * @param i_End
     * @return
     */
    public String getRange(String i_Key ,long i_Start ,long i_End)
    {
        return this.core_getRange(i_Key ,i_Start ,i_End);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��GETRANGE key start end
     *
     * ���� key ���ַ���ֵ�����ַ������ַ����Ľ�ȡ��Χ�� start �� end ����ƫ��������(���� start �� end ����)��
     * ����ƫ������ʾ���ַ������ʼ������ -1 ��ʾ���һ���ַ��� -2 ��ʾ�����ڶ������Դ����ơ�
     * GETRANGE ͨ����֤���ַ�����ֵ��(range)������ʵ���ַ�����ֵ������������Χ��ֵ������
     * 
     * @param i_Key
     * @param i_Start
     * @param i_End
     * @return
     */
    private String core_getRange(String i_Key ,long i_Start ,long i_End)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        String       v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.getrange(i_Key ,i_Start ,i_End);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��APPEND key value
     * 
     * ��� key �Ѿ����ڲ�����һ���ַ����� APPEND ��� value ׷�ӵ� key ԭ����ֵ��ĩβ��
     * ��� key �����ڣ� APPEND �ͼ򵥵ؽ����� key ��Ϊ value ������ִ�� SET key value һ����
     * 
     * @param i_Key
     * @param i_Value
     * @return
     */
    public boolean append(String i_Key ,String i_Value)
    {
        return this.core_append(i_Key ,i_Value);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��APPEND key value
     * 
     * ��� key �Ѿ����ڲ�����һ���ַ����� APPEND ��� value ׷�ӵ� key ԭ����ֵ��ĩβ��
     * ��� key �����ڣ� APPEND �ͼ򵥵ؽ����� key ��Ϊ value ������ִ�� SET key value һ����
     * 
     * @param i_Key
     * @param i_Value
     * @return
     */
    private boolean core_append(String i_Key ,String i_Value)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        if ( i_Value == null || i_Value.length() <= 0 )  // ����ո�û����JavaHelp.isNull()
        {
            throw new NullPointerException("Value is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            v_Ret = v_ShardedJedis.append(i_Key ,i_Value) > 0;
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = false;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * ��������
     * 
     * �� key �д��������ֵ��һ��
     * 
     * ��� key �����ڣ���ô key ��ֵ���ȱ���ʼ��Ϊ 0 ��Ȼ����ִ�� INCR ������
     * ���ֵ������������ͣ����ַ������͵�ֵ���ܱ�ʾΪ���֣���ô����һ������
     * ��������ֵ������ 64 λ(bit)�з������ֱ�ʾ֮�ڡ�
     * 
     * @param i_Key    
     * @return         �쳣ʱ����null
     */
    public Long increment(String i_Key)
    {
        return this.core_IncrementDecrement(i_Key ,1);
    }
    
    
    
    /**
     * �ݼ�����
     * 
     * �� key �д��������ֵ��һ��
     * 
     * ��� key �����ڣ���ô key ��ֵ���ȱ���ʼ��Ϊ 0 ��Ȼ����ִ�� DECR ������
     * ���ֵ������������ͣ����ַ������͵�ֵ���ܱ�ʾΪ���֣���ô����һ������
     * ��������ֵ������ 64 λ(bit)�з������ֱ�ʾ֮�ڡ�
     * 
     * @param i_Key
     * @return         �쳣ʱ����null
     */
    public Long decrement(String i_Key)
    {
        return this.core_IncrementDecrement(i_Key ,-1);
    }
    
    
    
    /**
     * ������ݼ�����
     * 
     * ԭʼ����Ϊ��INCRBY key increment
     * ԭʼ����Ϊ��DECRBY key decrement
     * 
     * �� key �������ֵ�������� increment ��
     * ��� key �����ڣ���ô key ��ֵ���ȱ���ʼ��Ϊ 0 ��Ȼ����ִ�� INCRBY ���
     * ���ֵ������������ͣ����ַ������͵�ֵ���ܱ�ʾΪ���֣���ô����һ������
     * ��������ֵ������ 64 λ(bit)�з������ֱ�ʾ֮�ڡ�
     * 
     * @param i_Key    
     * @param i_Value  ��ֵΪ��ӣ���ֵΪ����
     * @return         �쳣ʱ����null
     */
    public Long increment(String i_Key ,long i_Value)
    {
        return this.core_IncrementDecrement(i_Key ,i_Value);
    }
    
    
    
    /**
     * ������ݼ�����
     * 
     * ԭʼ����Ϊ��INCRBY key increment
     * ԭʼ����Ϊ��DECRBY key decrement
     * 
     * �� key �������ֵ�������� increment ��
     * ��� key �����ڣ���ô key ��ֵ���ȱ���ʼ��Ϊ 0 ��Ȼ����ִ�� INCRBY ���
     * ���ֵ������������ͣ����ַ������͵�ֵ���ܱ�ʾΪ���֣���ô����һ������
     * ��������ֵ������ 64 λ(bit)�з������ֱ�ʾ֮�ڡ�
     * 
     * @param i_Key    
     * @param i_Value  ��ֵΪ��ӣ���ֵΪ����
     * @return         �쳣ʱ����null
     */
    public Long decrement(String i_Key ,long i_Value)
    {
        return this.core_IncrementDecrement(i_Key ,i_Value);
    }
    
    
    
    /**
     * ������ݼ�����
     * 
     * ԭʼ����Ϊ��INCRBY key increment
     * ԭʼ����Ϊ��DECRBY key decrement
     * 
     * �� key �������ֵ�������� increment ��
     * ��� key �����ڣ���ô key ��ֵ���ȱ���ʼ��Ϊ 0 ��Ȼ����ִ�� INCRBY ���
     * ���ֵ������������ͣ����ַ������͵�ֵ���ܱ�ʾΪ���֣���ô����null
     * ��������ֵ������ 64 λ(bit)�з������ֱ�ʾ֮�ڡ�
     * 
     * @param i_Key    
     * @param i_Value  ��ֵΪ��ӣ���ֵΪ����
     * @return         �쳣ʱ����null
     */
    private Long core_IncrementDecrement(String i_Key ,long i_Value)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        Long         v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            v_Ret = v_ShardedJedis.incrBy(i_Key ,i_Value);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SET key value
     * 
     * ���ַ���ֵ value ������ key ��
     * 
     * ��� key �Ѿ���������ֵ�� SET �͸�д��ֵ����������
     * 
     * @param i_Key    �ؼ���
     * @param i_Value  ֵ
     * @return         ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    public boolean put(String i_Key ,String i_Value)
    {
        return this.core_put(new RData(i_Key ,i_Value));
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SETNX key value
     * 
     * �� key ��ֵ��Ϊ value �����ҽ��� key �����ڡ�
     * 
     * �������� key �Ѿ����ڣ��� SETNX �����κζ�����
     * 
     * @param i_Key    �ؼ���
     * @param i_Value  ֵ
     * @return         ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    public boolean putNotExists(String i_Key ,String i_Value)
    {
        RData v_RData = new RData(i_Key ,i_Value);
        
        v_RData.setPutType(RData.PutType.NX);
        
        return this.core_put(v_RData);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SET key value EX seconds NX
     * 
     * �� key ��ֵ��Ϊ value �����ҽ��� key ������ʱ������ key ������ʱ����Ϊ seconds (����Ϊ��λ)��
     * 
     * �������� key �Ѿ����ڣ��� SETNX �����κζ�����
     * 
     * @param i_Key      �ؼ���
     * @param i_Value    ֵ
     * @param i_Seconds  ����ʱ��(����Ϊ��λ)
     * @return           ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    public boolean putNotExists(String i_Key ,String i_Value ,int i_Seconds)
    {
        if ( i_Seconds < 1 )
        {
            throw new VerifyError("ExpireTime Seconds < 1.");
        }
        
        RData v_RData = new RData(i_Key ,i_Value);
        
        v_RData.setPutType(   RData.PutType.NX);
        v_RData.setExpireTime(i_Seconds);
        
        return this.core_put(v_RData);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SETNX key value
     * 
     * ��ֵ value ������ key ������ key ������ʱ����Ϊ seconds (����Ϊ��λ)��
     * 
     * ��� key �Ѿ����ڣ� SETEX �����д��ֵ��
     * 
     * @param i_Key
     * @param i_Value
     * @param i_Seconds  ����ʱ��(����Ϊ��λ)
     * @return           ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    public boolean put(String i_Key ,String i_Value ,int i_Seconds)
    {
        if ( i_Seconds < 1 )
        {
            throw new VerifyError("ExpireTime Seconds < 1.");
        }
        
        RData v_RData = new RData(i_Key ,i_Value);
        
        v_RData.setExpireTime(i_Seconds);
        
        return this.core_put(v_RData);
    }
    
    
    
    /**
     * �μ��� core_put(...)����
     * 
     * @param i_RData
     * @return         ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    public boolean put(RData i_RData)
    {
        return this.core_put(i_RData);
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SET key value [EX seconds] [PX milliseconds] [NX|XX]
     * 
     * ���ַ���ֵ value ������ key ��
     * 
     * ��� key �Ѿ���������ֵ�� SET �͸�д��ֵ���������͡�
     * 
     * ����ĳ��ԭ����������ʱ�䣨TTL���ļ���˵�� �� SET ����ɹ����������ִ��ʱ�� �����ԭ�е� TTL ���������
     * 
     * EX second �����ü��Ĺ���ʱ��Ϊ second �롣 SET key value EX second Ч����ͬ�� SETEX key second value ��
     * PX millisecond �����ü��Ĺ���ʱ��Ϊ millisecond ���롣 SET key value PX millisecond Ч����ͬ�� PSETEX key millisecond value ��
     * 
     * NX ��ֻ�ڼ�������ʱ���ŶԼ��������ò����� SET key value NX Ч����ͬ�� SETNX key value ��
     * XX ��ֻ�ڼ��Ѿ�����ʱ���ŶԼ��������ò�����
     * 
     * @param i_RData
     * @return         ����ֵ��ʾ�Ƿ�Setִֵ�гɹ�
     */
    private boolean core_put(RData i_RData)
    {
        if ( i_RData == null )
        {
            throw new NullPointerException("RData is null.");
        }
        
        if ( i_RData.getKey() == null )
        {
            throw new NullPointerException("RData.Key is null.");
        }
        
        if ( i_RData.getValue() == null )
        {
            throw new NullPointerException("RData.Value is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( i_RData.getPutType() == RData.PutType.Normal && i_RData.getExpireTime() <= 0 )
            {
                v_ShardedJedis.set(i_RData.getKey() ,i_RData.getValue());
            }
            else if ( i_RData.getPutType() != RData.PutType.Normal && i_RData.getExpireTime() > 0 )
            {
                v_ShardedJedis.set(i_RData.getKey() ,i_RData.getValue() ,$PutType_NXXX[i_RData.getPutType().ordinal()] ,$ExpireTimeType ,i_RData.getExpireTime());
            }
            else if ( i_RData.getExpireTime() > 0 )
            {
                v_ShardedJedis.setex(i_RData.getKey() ,i_RData.getExpireTime() ,i_RData.getValue());
            }
            else if ( i_RData.getPutType() == RData.PutType.NX )
            {
                v_Ret = v_ShardedJedis.setnx(i_RData.getKey() ,i_RData.getValue()) == 1L;
            }
            else if ( i_RData.getPutType() == RData.PutType.XX )
            {
                // APIû���ṩ�����Ĺ���
                v_Ret = false;
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = false;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    public boolean puts(List<RData> i_RDatas)
    {
        return this.core_puts(i_RDatas.toArray(new RData[]{}));
    }
    
    
    
    /**
     * ����Ⱥ����û�����⡣���༯Ⱥ�ֲ�ʽ����������
     * 
     * @param i_RDatas
     * @return
     */
    private boolean core_puts(RData [] i_RDatas)
    {
        if ( JavaHelp.isNull(i_RDatas) )
        {
            throw new NullPointerException("RDatas is null.");
        }
        
        for (int i=0; i<i_RDatas.length; i++)
        {
            RData v_RData = i_RDatas[i];
            
            if ( v_RData == null )
            {
                throw new NullPointerException("RData[" + i + "] is null.");
            }
            
            if ( v_RData.getKey() == null )
            {
                throw new NullPointerException("RData[" + i + "].Key is null.");
            }
            
            if ( v_RData.getValue() == null )
            {
                throw new NullPointerException("RData[" + i + "].Value is null.");
            }
        }
        
        ShardedJedis v_ShardedJedis = null;
        Transaction  v_Transaction  = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            v_Transaction = v_ShardedJedis.getAllShards().iterator().next().multi();
            
            for (int i=0; i<i_RDatas.length; i++)
            {
                RData v_RData = i_RDatas[i];
                
                v_Transaction.set(v_RData.getKey() ,v_RData.getValue());
            }
            
            v_Transaction.exec();
            v_Ret = true;
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = false;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
}
