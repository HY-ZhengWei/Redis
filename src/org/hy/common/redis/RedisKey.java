package org.hy.common.redis;

import java.util.List;
import java.util.Set;

import org.hy.common.JavaHelp;

import redis.clients.jedis.ShardedJedis;





/**
 * Redis���ݿ��е�Key�������ز�����
 * 
 * @author ZhengWei(HY)
 * @create 2014-09-17
 */
public class RedisKey
{
    private Redis redis;
    
    
    
    public RedisKey(Redis i_Redis)
    {
        if ( i_Redis == null )
        {
            throw new NullPointerException("Redis is null.");
        }
        
        this.redis = i_Redis;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��EXISTS key
     * 
     * ������ key �Ƿ����
     * 
     * @param i_Key
     * @return
     */
    public boolean isExists(String i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.exists(i_Key);
            
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
     * ԭʼ����Ϊ��TYPE key
     * 
     * ���� key �������ֵ�����͡�����
     * 
     *   1. none    (key������) -- ���޸�Ϊ���� null
     *   2. string  (�ַ���)
     *   3. list    (�б�)
     *   4. set     (����)
     *   5. zset    (����)
     *   6. hash    (��ϣ��)
     * 
     * @param i_Key
     * @return
     */
    public String getKeyType(String i_Key)
    {
        if ( JavaHelp.isNull(i_Key) )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        String       v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.type(i_Key);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        if ( v_Ret == null )
        {
            return v_Ret;
        }
        else
        {
            return "none".equals(v_Ret) ? null : v_Ret;
        }
    }
    
    
    
    /**
     * ԭʼ����Ϊ��EXPIRE key seconds
     * 
     * Ϊ���� key ��������ʱ�䣬�� key ����ʱ(����ʱ��Ϊ 0 )�����ᱻ�Զ�ɾ����
     * 
     * ����ʱ�����ͨ��ʹ�� DEL ������ɾ������ key ���Ƴ���
     * ���߱� SET �� GETSET ���д(overwrite)������ζ�ţ�
     * 
     * ���һ������ֻ���޸�(alter)һ��������ʱ��� key ��ֵ��
     * ��������һ���µ� key ֵ������(replace)���Ļ�����ô����ʱ�䲻�ᱻ�ı䡣
     * 
     * ��һ���棬���ʹ�� RENAME ��һ�� key ���и�������ô������� key ������ʱ��͸���ǰһ����
     * 
     * @param i_Key
     * @return
     */
    public boolean expire(String i_Key ,int i_Seconds)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        if ( i_Seconds <= 0 )
        {
            throw new IndexOutOfBoundsException("i_Seconds <= 0.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            v_Ret = v_ShardedJedis.expire(i_Key ,i_Seconds) == 1;
            
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
     * ԭʼ����Ϊ��EXPIRE key seconds
     * 
     * �Ƴ����� key ������ʱ�䣬����� key �ӡ���ʧ�ġ�(������ʱ�� key )��
     * ת���ɡ��־õġ�(һ����������ʱ�䡢�������ڵ� key )
     * 
     * @param i_Key
     * @return
     */
    public boolean delExpire(String i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        ShardedJedis v_ShardedJedis = null;
        boolean      v_Ret          = false;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            v_Ret = v_ShardedJedis.persist(i_Key) == 1;
            
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
     * ԭʼ����Ϊ��TTL key
     * 
     * ����Ϊ��λ�����ظ��� key ��ʣ������ʱ��(TTL, time to live)��
     * 
     * @param i_Key
     * @return       �� key ������ʱ������ -2 ��
     *               �� key ���ڵ�û������ʣ������ʱ��ʱ������ -1 ��
     *               ��������Ϊ��λ������ key ��ʣ������ʱ�䡣
     */
    public Long timeToLive(String i_Key)
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
            
            v_Ret = v_ShardedJedis.ttl(i_Key);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret = null;
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
    public void dels(String ... i_Keys)
    {
        this.core_dels(i_Keys);
    }
    
    
    
    public void dels(Set<String> i_Keys)
    {
        this.core_dels(i_Keys.toArray(new String []{}));
    }
    
    
    
    public void dels(List<String> i_Keys)
    {
        this.core_dels(i_Keys.toArray(new String []{}));
    }
    
    
    
    /**
     * ԭʼ����Ϊ��DEL key [key ...]
     * 
     * ɾ��������һ������ key ��
     * 
     * �����ڵ� key �ᱻ���ԡ�
     * 
     * @param i_Keys
     */
    private void core_dels(String [] i_Keys)
    {
        if ( i_Keys == null )
        {
            throw new NullPointerException("Keys is null.");
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] == null )
            {
                throw new NullPointerException("Keys[" + i + "] is null.");
            }
        }
        
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            for (int i=0; i<i_Keys.length; i++)
            {
                v_ShardedJedis.del(i_Keys[i]);
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
}
