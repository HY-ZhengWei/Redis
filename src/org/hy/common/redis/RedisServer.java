package org.hy.common.redis;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Date;
import org.hy.common.JavaHelp;
import org.hy.common.Return;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;





/**
 * Redis���ݿ��е�Server����������ز�����
 * 
 * @author ZhengWei(HY)
 * @create 2014-10-17
 */
public class RedisServer
{
    private Redis redis;
    
    
    
    public RedisServer(Redis i_Redis)
    {
        if ( i_Redis == null )
        {
            throw new NullPointerException("Redis is null.");
        }
        
        this.redis = i_Redis;
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��bgsave�������зֲ�ʽ���ݿ�
     * 
     * ԭʼ����Ϊ��BGSAVE
     * 
     * �ں�̨�첽(Asynchronously)���浱ǰ���ݿ�����ݵ����̡�
     * 
     * BGSAVE ����ִ��֮���������� OK ��Ȼ�� Redis fork ��һ�����ӽ��̣�
     * ԭ���� Redis ����(������)��������ͻ������󣬶��ӽ����������ݱ��浽���̣�Ȼ���˳���
     */
    public void saveBackground()
    {
        this.core_saveBackground(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��ĳһ�׼�Ⱥ���ݿ⣬ִ�б���bgsave����
     * 
     * ԭʼ����Ϊ��BGSAVE
     * 
     * �ں�̨�첽(Asynchronously)���浱ǰ���ݿ�����ݵ����̡�
     * 
     * BGSAVE ����ִ��֮���������� OK ��Ȼ�� Redis fork ��һ�����ӽ��̣�
     * ԭ���� Redis ����(������)��������ͻ������󣬶��ӽ����������ݱ��浽���̣�Ȼ���˳���
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ�ִ��bgsave����
     */
    public void saveBackground(String i_ShardName)
    {
        this.core_saveBackground(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��bgsave�������зֲ�ʽ���ݿ�
     * 
     * ԭʼ����Ϊ��BGSAVE
     * 
     * �ں�̨�첽(Asynchronously)���浱ǰ���ݿ�����ݵ����̡�
     * 
     * BGSAVE ����ִ��֮���������� OK ��Ȼ�� Redis fork ��һ�����ӽ��̣�
     * ԭ���� Redis ����(������)��������ͻ������󣬶��ӽ����������ݱ��浽���̣�Ȼ���˳���
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ�ִ��bgsave����
     */
    private void core_saveBackground(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                for (Jedis v_Jedis : v_ShardedJedis.getAllShards())
                {
                    v_Jedis.bgsave();
                }
            }
            else
            {
                v_ShardedJedis.getShard(i_ShardName).bgsave();
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��save�������зֲ�ʽ���ݿ�
     * 
     * ԭʼ����Ϊ��SAVE
     * 
     * ����ִ��һ��ͬ�����������
     * ����ǰ Redis ʵ�����������ݿ���(snapshot)�� RDB �ļ�����ʽ���浽Ӳ�̡�
     * 
     * һ����˵����������������ִ�� SAVE ��������Ϊ�����������пͻ��ˣ�
     * �������ݿ������ͨ���� BGSAVE �����첽��ִ�С�
     * Ȼ����������𱣴����ݵĺ�̨�ӽ��̲��ҳ�������ʱ�� 
     * SAVE ������Ϊ�������ݵ�����ֶ���ʹ�á�
     */
    public void save()
    {
        this.core_save(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��ĳһ�׼�Ⱥ���ݿ⣬ִ�б���save����
     * 
     * ԭʼ����Ϊ��SAVE
     * 
     * ����ִ��һ��ͬ�����������
     * ����ǰ Redis ʵ�����������ݿ���(snapshot)�� RDB �ļ�����ʽ���浽Ӳ�̡�
     * 
     * һ����˵����������������ִ�� SAVE ��������Ϊ�����������пͻ��ˣ�
     * �������ݿ������ͨ���� BGSAVE �����첽��ִ�С�
     * Ȼ����������𱣴����ݵĺ�̨�ӽ��̲��ҳ�������ʱ�� 
     * SAVE ������Ϊ�������ݵ�����ֶ���ʹ�á�
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ�ִ��save����
     */
    public void save(String i_ShardName)
    {
        this.core_save(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ִ��save�������зֲ�ʽ���ݿ�
     * 
     * ԭʼ����Ϊ��SAVE
     * 
     * ����ִ��һ��ͬ�����������
     * ����ǰ Redis ʵ�����������ݿ���(snapshot)�� RDB �ļ�����ʽ���浽Ӳ�̡�
     * 
     * һ����˵����������������ִ�� SAVE ��������Ϊ�����������пͻ��ˣ�
     * �������ݿ������ͨ���� BGSAVE �����첽��ִ�С�
     * Ȼ����������𱣴����ݵĺ�̨�ӽ��̲��ҳ�������ʱ�� 
     * SAVE ������Ϊ�������ݵ�����ֶ���ʹ�á�
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ�ִ��save����
     */
    private void core_save(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                for (Jedis v_Jedis : v_ShardedJedis.getAllShards())
                {
                    v_Jedis.save();
                }
            }
            else
            {
                v_ShardedJedis.getShard(i_ShardName).save();
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ������»�ȡ���зֲ�ʽ���ݿ�ĺϼ�Keys����
     * 
     * ԭʼ����Ϊ��DBSIZE
     * 
     * ���ص�ǰ���ݿ�� key ��������
     * 
     * @return             �쳣���� null
     */
    public Long dbSize()
    {
        return this.core_dbSize(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ������»�ȡĳһ�׼�Ⱥ���ݿ⣬��Keys����
     * 
     * ԭʼ����Ϊ��DBSIZE
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ����ȡ���м�Ⱥ���ݿ��Keys����
     * @return             �쳣���� null
     */
    public Long dbSize(String i_ShardName)
    {
        return this.core_dbSize(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ������»�ȡĳһ�׼�Ⱥ���ݿ⣬��Keys����
     * 
     * ԭʼ����Ϊ��DBSIZE
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ����ȡ���м�Ⱥ���ݿ��Keys����
     * @return             �쳣���� null
     */
    private Long core_dbSize(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        Long         v_Ret          = 0L;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                for (Jedis v_Jedis : v_ShardedJedis.getAllShards())
                {
                    v_Ret += v_Jedis.dbSize();
                }
            }
            else
            {
                v_Ret = v_ShardedJedis.getShard(i_ShardName).dbSize();
            }
            
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
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ������»�ȡĳһ�׼�Ⱥ���ݿ⣬���һ�� Redis �ɹ������ݱ��浽�����ϵ�ʱ��
     * 
     * ԭʼ����Ϊ��LASTSAVE
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ������
     * @return             �쳣���� null
     */
    public Date saveLastTime(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        Date         v_Ret          = null;
        
        try
        {
            v_ShardedJedis = this.redis.getReader();
            v_Ret = new Date(v_ShardedJedis.getShard(i_ShardName).lastsave());
            
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
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ����������зֲ�ʽ��Ⱥ���ݿ⣬ִ��flushDB����
     * 
     * ԭʼ����Ϊ��FLUSHDB
     * 
     * ��յ�ǰ���ݿ��е����� key��
     */
    public void flushDB()
    {
        this.core_flushDB(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��flushDB����
     * 
     * ԭʼ����Ϊ��FLUSHDB
     * 
     * ��յ�ǰ���ݿ��е����� key��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��flushDB����
     */
    public void flushDB(String i_ShardName)
    {
        this.core_flushDB(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��flushDB����
     * 
     * ԭʼ����Ϊ��FLUSHDB
     * 
     * ��յ�ǰ���ݿ��е����� key��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��flushDB����
     */
    private void core_flushDB(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                for (Jedis v_Jedis : v_ShardedJedis.getAllShards())
                {
                    v_Jedis.flushDB();
                }
            }
            else
            {
                v_ShardedJedis.getShard(i_ShardName).flushDB();
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ����������зֲ�ʽ��Ⱥ���ݿ⣬ִ��flushAll����
     * 
     * ԭʼ����Ϊ��FLUSHALL
     * 
     * ������� Redis ������������(ɾ���������ݿ������ key )��
     */
    public void flushDBAll()
    {
        this.core_flushDBAll(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��flushAll����
     * 
     * ԭʼ����Ϊ��FLUSHALL
     * 
     * ������� Redis ������������(ɾ���������ݿ������ key )��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��flushAll����
     */
    public void flushDBAll(String i_ShardName)
    {
        this.core_flushDBAll(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��flushAll����
     * 
     * ԭʼ����Ϊ��FLUSHALL
     * 
     * ������� Redis ������������(ɾ���������ݿ������ key )��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��flushAll����
     */
    private void core_flushDBAll(String i_ShardName)
    {
        ShardedJedis v_ShardedJedis = null;
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                for (Jedis v_Jedis : v_ShardedJedis.getAllShards())
                {
                    v_Jedis.flushAll();
                }
            }
            else
            {
                v_ShardedJedis.getShard(i_ShardName).flushAll();
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ����������зֲ�ʽ��Ⱥ���ݿ⣬ִ��ping����
     * 
     * ԭʼ����Ϊ��PING
     * 
     * ʹ�ÿͻ����� Redis ����������һ�� PING ��������������������Ļ����᷵��һ�� PONG ��
     * 
     * @return             ��ĳЩ��Ⱥ�쳣ʱ��Return.paramObj ��¼�����쳣�ļ�Ⱥ����
     */
    public Return<List<String>> ping()
    {
        return this.core_ping(null);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��ping����
     * 
     * ԭʼ����Ϊ��PING
     * 
     * ʹ�ÿͻ����� Redis ����������һ�� PING ��������������������Ļ����᷵��һ�� PONG ��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��ping����
     * @return             ��ĳЩ��Ⱥ�쳣ʱ��Return.paramObj ��¼�����쳣�ļ�Ⱥ����
     */
    public Return<List<String>> ping(String i_ShardName)
    {
        return this.core_ping(i_ShardName);
    }
    
    
    
    /**
     * Redis���ݿ��ڡ����׼�Ⱥ�ֲ�ʽ�������ĳһ�׼�Ⱥ(�����зֲ�ʽ��Ⱥ)���ݿ⣬ִ��ping����
     * 
     * ԭʼ����Ϊ��PING
     * 
     * ʹ�ÿͻ����� Redis ����������һ�� PING ��������������������Ļ����᷵��һ�� PONG ��
     * 
     * @param i_ShardName  �ֲ�ʽ��Ⱥ�м�Ⱥ�����ơ�Ϊ��ʱ�������м�Ⱥ���ݿ��ִ��ping����
     * @return             ��ĳЩ��Ⱥ�쳣ʱ��Return.paramObj ��¼�����쳣�ļ�Ⱥ����
     */
    private Return<List<String>> core_ping(String i_ShardName)
    {
        ShardedJedis         v_ShardedJedis = null;
        Return<List<String>> v_Ret          = new Return<List<String>>(true);
        
        try
        {
            v_ShardedJedis = this.redis.getWriter();
            
            if ( JavaHelp.isNull(i_ShardName) )
            {
                v_Ret.paramObj = new ArrayList<String>(this.redis.getShardSize());
                
                List<String> v_ShardNames = this.redis.getShardNames();
                
                if ( JavaHelp.isNull(v_ShardNames) )
                {
                    throw new NullPointerException("ShardInfos is null.");
                }
                
                for (int v_ShardIndex=v_ShardNames.size() - 1; v_ShardIndex>=0; v_ShardIndex--)
                {
                    String v_IsPONG = v_ShardedJedis.getShard(v_ShardNames.get(v_ShardIndex)).ping();
                    
                    if ( JavaHelp.isNull(v_IsPONG) || !"PONG".equals(v_IsPONG) )
                    {
                        v_Ret.set(false);
                        v_Ret.paramObj.add(v_ShardNames.get(v_ShardIndex));
                    }
                }
            }
            else
            {
                String v_IsPONG = v_ShardedJedis.getShard(i_ShardName).ping();
                
                if ( JavaHelp.isNull(v_IsPONG) || !"PONG".equals(v_IsPONG) )
                {
                    v_Ret.set(false);
                    v_Ret.paramObj = new ArrayList<String>(1);
                    v_Ret.paramObj.add(i_ShardName);
                }
            }
            
            this.redis.returnResource(v_ShardedJedis);
        }
        catch (Exception exce)
        {
            v_Ret.set(false);
            this.redis.returnResource(v_ShardedJedis ,exce);
        }
        
        return v_Ret;
    }
}
