package org.hy.common.redis;

import redis.clients.jedis.ShardedJedis;





/**
 * Redis���ݿ��е�Set�������ز�����
 * 
 * @author ZhengWei(HY)
 * @create 2014-09-16
 */
public class RedisSet
{
    
    private Redis redis;
    
    
    
    public RedisSet(Redis i_Redis)
    {
        if ( i_Redis == null )
        {
            throw new NullPointerException("Redis is null.");
        }
        
        this.redis = i_Redis;
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SADD key member [member ...]
     * 
     * ��һ������ member Ԫ�ؼ��뵽���� key ���У�
     * �Ѿ������ڼ��ϵ� member Ԫ�ؽ������ԡ�
     * 
     * ���� key �����ڣ��򴴽�һ��ֻ���� member Ԫ������Ա�ļ��ϡ�
     * �� key ���Ǽ�������ʱ������һ������
     * 
     * @param i_Key
     * @param i_Value
     */
    public void add(String i_Key ,String ... i_Value)
    {
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            v_ShardedJedis.sadd(i_Key ,i_Value);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
    
    
    /**
     * ԭʼ����Ϊ��SISMEMBER key member
     * 
     * �ж� member Ԫ���Ƿ񼯺� key �ĳ�Ա��
     * 
     * @param i_Key
     * @param i_Value
     * @return
     */
    public boolean isExists(String i_Key ,String i_Value)
    {
        boolean      v_Ret          = false;
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            
            v_Ret = v_ShardedJedis.sismember(i_Key ,i_Value);
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
    
    
    
}
