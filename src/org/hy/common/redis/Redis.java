package org.hy.common.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Date;
import org.hy.common.JavaHelp;
import org.hy.common.ListMap;
import org.hy.common.StringHelp;
import org.hy.common.thread.Job;
import org.hy.common.thread.Jobs;
import org.hy.common.xml.XJava;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;





/**
 * Redis���ݿ����
 * 
 * ��ɫ1�����в����������Ե��ͷ���Դ���ɱ��༰������ڲ��ͷţ���֤������Դ�İ�ȫ
 * 
 * ��ɫ2�������߼���ĸ��ͨ��������Ϣ����
 *          1. һ��Hash($MetaData_Tables):�������б���Ϣ��   Hash.key = �����ƣ�Hash.value = ��Ĵ���ʱ��
 *          2. ����Hash(������)          :�������������б�Hash.key = ��������Hash.value = �������Ĵ������޸�ʱ��
 *          3. ����Hash(������)          :����һ��������Ϣ��   Hash.key = �ֶ�����Hash.value = �ֶ�ֵ
 *        
 * ����1��������  RowKey������һ�����ݵ�Ψһ��ʾ
 *                      ע�⣺������Ĭ��Ϊ "������.ID" ����ʽ
 *                      
 * ����2���ؼ���  Key   ��Redis���ݿ���һ��Key-Value��Keyֵ������Map���ϵ�Keyֵ��
 *          
 * ע���ڶ��󹹽�ʱ���ѱ�����XJava�У���XIDΪthis.getXID()
 *          
 * @author ZhengWei(HY)
 * @create 2014-09-16
 */
public class Redis
{
    
    /** ������������ */
    public enum RowKeyType
    {
        /** �нṹ��ʽ�ģ�"������.ID"��Ĭ�� */
        $TableName_ID
        
        /** �޽ṹ������������ʱ��������ID��ͬ */
       ,$ID
    }
    
    /** 
     * Redis������ģʽ
     * ��Jedis���ö��׼�Ⱥ�����÷ֲ�ʽʱ����������Ϊ�Ŀ���������ģʽ
     */
    public enum RunMode
    {
        /** 
         * �ֲ�ʽ��Ⱥģʽ(��Ƭ��) 
         * 
         * ����Jedis��Redis������Ĺ��ܡ�
         * ����Key���������򣬽���ͬKey�����ڲ�ͬRedis������������
         * 
         * �������˶��׼�Ⱥʱ��Ĭ�����ô�ģʽ
         */
        $Shard
        
        /**
         * ��������ģʽ
         * 
         * ������Ϊ�Ľ����׼�Ⱥ���������ȫһ����
         * ͬʱ����������ģʽ�£��Զ����ݲ���ʵ��������
         * 
         * ��ֻ������Ψһһ�׼�Ⱥʱ��Ĭ�����ô�ģʽ����Ϊ�������������
         * 
         * ���׼�Ⱥ�����������һ�׼�Ⱥ�쳣�޷�����ʱ���Զ������޳����������֮�С�
         * ͬʱ������̽��ģʽ�����̽���쳣�ļ�Ⱥ�Ƿ�ָ�������
         * ����ָ�����������ӵ��������֮�С�
         * ���쳣�ڼ�����ݲ�����ͬ����
         */
       ,$Backup
    }
    
    
    
    private static final String             $MetaData_Tables = "$MetaData_Tables";
    
    /** 
     * �ֲ�ʽ��Ⱥ��Ϣ
     * 
     * Memcached��ȫ���ڷֲ�ʽ��Ⱥ����Redis��Master-Slave��
     * ������Reids�����ɼ�Ⱥģʽ���������������Master-Slave��
     * ÿ��Master-Slave��ɸ��Ե����ִ���ͨ��Client���ߣ����һ���Թ�ϣ�� 
     * 
     * Map.key Ϊ�ֲ�ʽ��Ⱥ�м�Ⱥ������(�߼���Ψһ��ʶ)
     */
    private ListMap<String ,JedisShardInfo> shardInfoMap;
    
    /** �����쳣�ķֲ�ʽ��Ⱥ��Ϣ (ֻ���ڱ�������ģʽ) */
    private ListMap<String ,JedisShardInfo> exceptionShards;
    
    /** �ֲ�ʽ��Ⱥ����Դ�� */
    private ShardedJedisPool                shardedPool;
    
    private RedisKey                        redisKey;
    
    private RedisString                     redisString;
    
    private RedisSet                        redisSet;
    
    private RedisHash                       redisHash;
    
    private RedisServer                     redisServer;
    
    private String                          xjavaID;
    
    private RunMode                         runMode;
    
    private boolean                         isKeyOrder;
    
    private RowKeyType                      rowKeyType;
    
    private Job                             job;
    
    private Jobs                            jobs;
    
    
    
    public Redis(String i_IP)
    {
        this(null ,i_IP ,6379);
    }
    
    
    
    public Redis(String i_IP ,int i_Port)
    {
        this(null ,i_IP ,i_Port);
    }
    
    
    
    public Redis(JedisPoolConfig i_PoolConfig ,String i_IP ,int i_Port)
    {
        this(i_PoolConfig ,newOneList(i_IP ,i_Port ,null));
    }
    
    
    
    public Redis(JedisPoolConfig i_PoolConfig ,String i_IP ,int i_Port ,String i_Password)
    {
        this(i_PoolConfig ,newOneList(i_IP ,i_Port ,i_Password));
    }
    
    
    
    public Redis(List<JedisShardInfo> i_JedisShardInfos)
    {
        this(null ,i_JedisShardInfos);
    }
    
    
    
    public Redis(JedisPoolConfig i_PoolConfig ,List<JedisShardInfo> i_JedisShardInfos)
    {
        if ( JavaHelp.isNull(i_JedisShardInfos) )
        {
            throw new NullPointerException("HostAndPorts is null.");
        }
        
        shardInfoMap    = new ListMap<String ,JedisShardInfo>(i_JedisShardInfos.size() ,false);
        exceptionShards = new ListMap<String ,JedisShardInfo>(i_JedisShardInfos.size() ,false);
        
        for (JedisShardInfo v_Host : i_JedisShardInfos)
        {
            if ( JavaHelp.isNull(v_Host.getHost()) )
            {
                throw new NullPointerException("IP is null.");
            }
            
            if ( 0 >= v_Host.getPort() || v_Host.getPort() >= 65535 )
            {
                throw new RuntimeException("Port is not 0~65535.");
            }
            
            if ( JavaHelp.isNull(v_Host.getName()) )
            {
                String         v_Name  = v_Host.getHost() + ":" + v_Host.getPort();
                JedisShardInfo v_Clone = new JedisShardInfo(v_Host.getHost() ,v_Host.getPort() ,v_Host.getTimeout() ,v_Name);
                v_Clone.setPassword(v_Host.getPassword());
                
                if ( shardInfoMap.containsKey(v_Name) )
                {
                    throw new RuntimeException("JedisShardInfo name[" + v_Name + "] is same.");
                }
                shardInfoMap.put(v_Name ,v_Clone);
            }
            else
            {
                if ( shardInfoMap.containsKey(v_Host.getName()) )
                {
                    throw new RuntimeException("JedisShardInfo name[" + v_Host.getName() + "] is same.");
                }
                shardInfoMap.put(v_Host.getName() ,v_Host);
            }
        }
        
        JedisPoolConfig v_PoolConfig = i_PoolConfig;
        if ( v_PoolConfig == null )
        {
            v_PoolConfig = new JedisPoolConfig();
            v_PoolConfig.setMaxTotal(1024);
            v_PoolConfig.setMaxIdle(10);
            v_PoolConfig.setMinIdle(1);
        }
        
        shardedPool = new ShardedJedisPool(v_PoolConfig ,JavaHelp.toList(shardInfoMap));
        
        if ( this.shardInfoMap.size() <= 1 )
        {
            this.runMode = RunMode.$Backup;
        }
        else
        {
            this.runMode = RunMode.$Shard;
        }
        
        this.redisKey    = new RedisKey(this);
        this.redisString = new RedisString(this);
        this.redisSet    = new RedisSet(this);
        this.redisHash   = new RedisHash(this);
        this.redisServer = new RedisServer(this);
        this.isKeyOrder  = true;
        this.rowKeyType  = RowKeyType.$TableName_ID;
        this.xjavaID     = "XID_REDIS_" + StringHelp.getUUID();
        XJava.putObject(this.xjavaID ,this);
    }
    
    
    
    protected static List<JedisShardInfo> newOneList(String i_IP ,int i_Port ,String i_Password)
    {
        List<JedisShardInfo> v_Ret = new ArrayList<JedisShardInfo>(1);
        
        v_Ret.add(new JedisShardInfo(i_IP ,i_Port));
        
        if ( i_Password != null )
        {
            v_Ret.get(0).setPassword(i_Password);
        }
        
        return v_Ret;
    }
    
    
    
    protected Map<String ,String> newMap()
    {
        if ( this.isKeyOrder )
        {
            return new LinkedHashMap<String ,String>();
        }
        else
        {
            return new HashMap<String ,String>();
        }
    }
    
    
    
    protected Map<String ,Map<String ,String>> newMapMap()
    {
        if ( this.isKeyOrder )
        {
            return new LinkedHashMap<String ,Map<String ,String>>();
        }
        else
        {
            return new HashMap<String ,Map<String ,String>>();
        }
    }
    
    
    
    protected Map<String ,List<String>> newMapList()
    {
        if ( this.isKeyOrder )
        {
            return new LinkedHashMap<String ,List<String>>();
        }
        else
        {
            return new HashMap<String ,List<String>>();
        }
    }
    
    
    
    protected Map<String ,Set<String>> newMapSet()
    {
        if ( this.isKeyOrder )
        {
            return new LinkedHashMap<String ,Set<String>>();
        }
        else
        {
            return new HashMap<String ,Set<String>>();
        }
    }
    
    
    
    /**
     * ��ȡд����
     * 
     * @return
     */
    protected ShardedJedis getWriter()
    {
        return this.shardedPool.getResource();
    }
    
    
    
    /**
     * ��ȡ������
     * 
     * @return
     */
    protected ShardedJedis getReader()
    {
        return this.shardedPool.getResource();
    }
    
    
    
    /**
     * ����д��ɺ����ִ�д˷����ͷ���Դ
     * 
     * �� returnResource ��д������������ԭ���ǣ�
     *    ����һ�� i_Exce == null ���жϣ��������
     * 
     * @param i_ShardedJedis
     */
    protected void returnResource(ShardedJedis i_ShardedJedis)
    {
        try
        {
            shardedPool.returnResource(i_ShardedJedis);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
            
            // �ͷ�Redis����
            // �ڳ������ʱ���������returnBrokenResource������pool��
            // �����´�ͨ��getResource�õ���instance�Ļ��������ܻ��������ݣ���������
            shardedPool.returnBrokenResource(i_ShardedJedis);
        }
    }
    
    
    
    /**
     * ����д��ɺ����ִ�д˷����ͷ���Դ
     * 
     * �� returnResource ��д������������ԭ���ǣ�
     *    �����ܵļ���һ�� i_Exce == null ���жϣ��������
     * 
     * @param i_ShardedJedis
     * @param i_Exce
     */
    protected void returnResource(ShardedJedis i_ShardedJedis ,Exception i_Exce)
    {
        if ( i_ShardedJedis == null )
        {
            if ( i_Exce != null )
            {
                i_Exce.printStackTrace();
            }
        }
        
        try
        {
            if ( i_Exce == null )
            {
                // ���������ӳء����������ʹ�ô˷���
                shardedPool.returnResource(i_ShardedJedis);
            }
            else
            {
                i_Exce.printStackTrace();
                
                // �ͷ�Redis����
                // �ڳ������ʱ���������returnBrokenResource������pool��
                // �����´�ͨ��getResource�õ���instance�Ļ��������ܻ��������ݣ���������
                shardedPool.returnBrokenResource(i_ShardedJedis);
                shardedPool.returnResource(      i_ShardedJedis);
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    @Override
    protected void finalize()
    {
        shardedPool.destroy();
    }
    
    
    
    public synchronized void createTable(String i_TableName)
    {
        if ( this.getRKey().isExists(i_TableName) )
        {
            throw new RuntimeException("TableName[" + i_TableName + "] is exists.");
        }
        
        if ( !this.getRHash().isExists($MetaData_Tables ,i_TableName) )
        {
            this.getRHash().put(true ,$MetaData_Tables ,i_TableName ,"" + Date.getNowTime().getTime());
        }
        
        // ʹ�ÿ��ֶ�ʵ��Ԥռ�õĴ�����Hash����
        this.getRHash().put(true ,i_TableName ,"" ,"");
        
        // ɾ���򴴽����Ԥռ�õĿ��ֶ���Ϣ
        // ע�⣺��Hash��û����Ԫ��(��)ʱ��������Ҳ�͸�ɾ���ˡ�
        // this.getRHash().del(i_TableName ,"");
    }
    
    
    
    /**
     * ��ձ�����
     * 
     * �������������Ϣ��û��ɾ��
     * 
     * @param i_TableName
     */
    private void core_deleteAll(String i_TableName)
    {
        Set<String> v_RowKeys = this.getRHash().getFields(i_TableName);
        
        // �򴴽����Ԥռ�õĿ��ֶ���Ϣ��
        // �����ų����ⲻɾ������Ϊ����յ�������û����ʵ��Ӧ����Ϣ
        v_RowKeys.remove("");
        
        this.getRKey().dels(v_RowKeys);
    }
    
    
    
    /**
     * ɾ����
     * 
     * @param i_TableName
     */
    public synchronized void dropTable(String i_TableName)
    {
        this.core_deleteAll(i_TableName);
        
        // ɾ������������������Ϣ
        this.getRKey().dels(i_TableName);
        
        // ɾ�����Ԫ������Ϣ
        this.getRHash().del(true ,$MetaData_Tables ,i_TableName);
    }
    
    
    
    /**
     * ��ձ�����
     * 
     * ʵ����Drop����ؽ�
     * 
     * @param i_TableName
     */
    public synchronized void deleteAll(String i_TableName)
    {
        this.dropTable(i_TableName);
        
        this.createTable(i_TableName);
    }
    
    
    
    public void truncate(String i_TableName)
    {
        this.deleteAll(i_TableName);
    }
    
    
    
    /**
     * �жϱ��Ƿ����
     * 
     * @param i_TableName
     * @return
     */
    public boolean isExistsTable(String i_TableName)
    {
        if ( this.getRKey().isExists(i_TableName) )
        {
            if ( this.getRHash().isExists($MetaData_Tables ,i_TableName) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * ����һ��������
     * 
     * @param i_TableName
     * @param i_Key
     * @return
     */
    public String makeRowKey(String i_TableName ,String i_Key)
    {
        if ( this.rowKeyType == RowKeyType.$TableName_ID )
        {
            return i_TableName + "." + i_Key;
        }
        else
        {
            return i_Key;
        }
    }
    
    
    
    /**
     * ����һ��������
     * 
     * @param i_TableName
     * @param i_Keys
     * @return
     */
    public String [] makeRowKeys(String i_TableName ,List<String> i_Keys)
    {
        return this.makeRowKeys(i_TableName ,i_Keys.toArray(new String []{}));
    }
    
    
    
    /**
     * ����һ��������
     * 
     * @param i_TableName
     * @param i_Keys
     * @return
     */
    public String [] makeRowKeys(String i_TableName ,String [] i_Keys)
    {
        if ( this.rowKeyType == RowKeyType.$TableName_ID )
        {
            String [] v_RowKeys = new String[i_Keys.length];
            
            for (int i=0; i<i_Keys.length; i++)
            {
                v_RowKeys[i] = i_TableName + "." + i_Keys[i];
            }
            
            return v_RowKeys;
        }
        else
        {
            return i_Keys;
        }
    }
    
    
    
    /**
     * ����һ���е�һ���ֶε�����
     * 
     * @param i_TableName  ������
     * @param i_Key        ����ID--�ؼ���
     * @param i_Field      ��������
     * @param i_Value      ����ֵ
     * @return             ����������
     */
    public String insert(String i_TableName ,String i_Key ,String i_Field ,String i_Value)
    {
        return this.insert(i_TableName ,new RData(i_Key ,i_Field ,i_Value));
    }
    
    
    
    /**
     * ����һ���е�һ���ֶε�����
     * 
     * @param i_TableName  ������
     * @param i_RData      ������Ϣ
     * @return             ����������
     */
    public String insert(String i_TableName ,RData i_RData)
    {
        List<String> v_Ret = this.core_puts(i_TableName ,new RData[]{i_RData});
        
        return v_Ret.get(0);
    }
    
    
    
    /**
     * ����һ�л��������
     * 
     * @param i_TableName  ������
     * @param i_RDatas     ������Ϣ(��ͬ�еĶ���ֶ���Ϣ��RData��getKey()Ϊͬһֵ)
     * @return             ��˳�򷵻����������ϣ����������ĸ���Ϊʵ���и���������i_RDatas.length�ĸ���
     */
    public List<String> inserts(String i_TableName ,RData ... i_RDatas)
    {
        return this.core_puts(i_TableName ,i_RDatas);
    }
    
    
    
    /**
     * ����һ�л��������
     * 
     * @param i_TableName  ������
     * @param i_RDatas     ������Ϣ(��ͬ�еĶ���ֶ���Ϣ��RData��getKey()Ϊͬһֵ)
     * @return             ��˳�򷵻����������ϣ����������ĸ���Ϊʵ���и���������i_RDatas.length�ĸ���
     */
    public List<String> inserts(String i_TableName ,List<RData> i_RDatas)
    {
        return this.core_puts(i_TableName ,i_RDatas.toArray(new RData []{}));
    }
    
    
    
    /**
     * �޸�һ���е�һ���ֶε�����
     * 
     * @param i_TableName  ������
     * @param i_Key        ����ID
     * @param i_Field      ��������
     * @param i_Value      ����ֵ
     * @return             ����������
     */
    public String update(String i_TableName ,String i_Key ,String i_Field ,String i_Value)
    {
        return this.update(i_TableName ,new RData(i_Key ,i_Field ,i_Value));
    }
    
    
    
    /**
     * �޸�һ���е�һ���ֶε�����
     * 
     * @param i_TableName  ������
     * @param i_RData      ������Ϣ
     * @return             ����������
     */
    public String update(String i_TableName ,RData i_RData)
    {
        List<String> v_Ret = this.core_puts(i_TableName ,new RData[]{i_RData});
        
        return v_Ret.get(0);
    }
    
    
    
    /**
     * �޸�һ�л��������
     * 
     * @param i_TableName  ������
     * @param i_RDatas     ������Ϣ(��ͬ�еĶ���ֶ���Ϣ��RData��getKey()Ϊͬһֵ)
     * @return             ��˳�򷵻����������ϣ����������ĸ���Ϊʵ���и���������i_RDatas.length�ĸ���
     */
    public List<String> update(String i_TableName ,RData ... i_RDatas)
    {
        return this.core_puts(i_TableName ,i_RDatas);
    }
    
    
    
    /**
     * �޸�һ�л��������
     * 
     * @param i_TableName  ������
     * @param i_RDatas     ������Ϣ(��ͬ�еĶ���ֶ���Ϣ��RData��getKey()Ϊͬһֵ)
     * @return             ��˳�򷵻����������ϣ����������ĸ���Ϊʵ���и���������i_RDatas.length�ĸ���
     */
    public List<String> update(String i_TableName ,List<RData> i_RDatas)
    {
        return this.core_puts(i_TableName ,i_RDatas.toArray(new RData []{}));
    }
    
    
    
    /**
     * ������޸�һ�л��������
     * 
     * @param i_TableName  ������
     * @param i_RDatas     ������Ϣ(��ͬ�еĶ���ֶ���Ϣ��RData��getKey()Ϊͬһֵ)
     * @return             ��˳�򷵻����������ϣ����������ĸ���Ϊʵ���и���������i_RDatas.length�ĸ���
     */
    private List<String> core_puts(String i_TableName ,RData [] i_RDatas)
    {
        if ( !this.getRKey().isExists(i_TableName) )
        {
            throw new RuntimeException("TableName[" + i_TableName + "] is exists.");
        }
        
        if ( JavaHelp.isNull(i_RDatas) )
        {
            throw new NullPointerException("RDatas is null.");
        }
        
        for (int i=0; i<i_RDatas.length; i++)
        {
            RData v_RData = i_RDatas[i];
            if ( v_RData == null )
            {
                throw new NullPointerException("RDatas[" + i + "] is null.");
            }
            
            if ( v_RData.getKey() == null )
            {
                throw new NullPointerException("RDatas[" + i + "].key is null.");
            }
            
            if ( v_RData.getField() == null )
            {
                throw new NullPointerException("RDatas[" + i + "].field is null.");
            }
        }
        
        
        List<RData>  v_RDatas  = new ArrayList<RData>(i_RDatas.length * 2);
        List<String> v_RowKeys = new ArrayList<String>();
        
        for (int i=0; i<i_RDatas.length; i++)
        {
            RData  v_RData  = i_RDatas[i];
            String v_RowKey = this.makeRowKey(i_TableName ,v_RData.getKey());
            
            if ( !v_RowKeys.contains(v_RowKey) )
            {
                v_RowKeys.add(v_RowKey);
            }
            v_RDatas.add(new RData(i_TableName ,v_RowKey ,"" + Date.getNowTime().getTime()));
            v_RDatas.add(new RData(v_RowKey    ,v_RData.getField() ,JavaHelp.NVL(v_RData.getValue())));
        }
        
        this.getRHash().put(v_RDatas);
        
        return v_RowKeys;
    }
    
    
    
    /**
     * ɾ����
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     */
    public void deleteRow(String i_TableName ,String ... i_Keys)
    {
        this.core_deleteRow(i_TableName ,i_Keys);
    }
    
    
    
    /**
     * ɾ����
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     */
    public void deleteRow(String i_TableName ,List<String> i_Keys)
    {
        this.core_deleteRow(i_TableName ,i_Keys.toArray(new String []{}));
    }
    
    
    
    /**
     * ɾ����
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     */
    private void core_deleteRow(String i_TableName ,String [] i_Keys)
    {
        if ( JavaHelp.isNull(i_Keys) )
        {
            throw new NullPointerException("i_Keys is null.");
        }
        
        if ( !this.getRKey().isExists(i_TableName) )
        {
            throw new RuntimeException("TableName[" + i_TableName + "] is exists.");
        }
        
        String [] v_RowKeys = this.makeRowKeys(i_TableName ,i_Keys);
        
        this.getRHash().dels(i_TableName ,v_RowKeys);
        
        this.getRKey().dels(v_RowKeys);
    }
    
    
    
    /**
     * ɾ���ֶΡ�
     * 
     * ����������ݶ������ڣ�ͬʱɾ������Ϣ
     * 
     * @param i_TableName
     * @param i_RDatas
     */
    public void delete(String i_TableName ,RData ... i_RDatas)
    {
        this.core_delete(i_TableName ,i_RDatas);
    }
    
    
    
    /**
     * ɾ���ֶΡ�
     * 
     * ����������ݶ������ڣ�ͬʱɾ������Ϣ
     * 
     * @param i_TableName
     * @param i_RDatas
     */
    public void delete(String i_TableName ,List<RData> i_RDatas)
    {
        this.core_delete(i_TableName ,i_RDatas.toArray(new RData []{}));
    }
    
    
    
    /**
     * ɾ���ֶΡ�
     * 
     * ����������ݶ������ڣ�ͬʱɾ������Ϣ
     * 
     * @param i_TableName
     * @param i_RDatas
     */
    private void core_delete(String i_TableName ,RData [] i_RDatas)
    {
        if ( !this.getRKey().isExists(i_TableName) )
        {
            throw new RuntimeException("TableName[" + i_TableName + "] is exists.");
        }
        
        if ( JavaHelp.isNull(i_RDatas) )
        {
            throw new NullPointerException("RDatas is null.");
        }
        
        
        List<String> v_Keys = this.getRHash().dels(i_RDatas);
        
        if ( !JavaHelp.isNull(v_Keys) )
        {
            // ����������ݶ������ڣ�ͬʱɾ������Ϣ
            for (int i=v_Keys.size()-1; i>=0; i--)
            {
                if ( this.getRKey().isExists(v_Keys.get(i)) )
                {
                    v_Keys.remove(i);
                }
            }
            if ( !JavaHelp.isNull(v_Keys) )
            {
                this.getRHash().dels(i_TableName ,v_Keys);
            }
        }
    }
    
    
    
    /**
     * ��ȡһ�����ݵ������ֶ���Ϣ
     * 
     * @param i_TableName  ������
     * @param i_Key        ����ID--�ؼ��֡�ע�ⲻ��������
     * @return             Map.Key   Ϊ�ֶ�����
     *                     Map.Value Ϊ�ֶ�ֵ
     */
    public Map<String ,String> getRow(String i_TableName ,String i_Key)
    {
        return this.getRHash().getValues(this.makeRowKey(i_TableName ,i_Key));
    }
    
    
    
    /**
     * ��ȡ�������ݵ������ֶ���Ϣ
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     * @return        Map<String ,Map<String ,String>>.key       ��ʾһ��������
     *                Map<String ,Map<String ,String>>.Map       ��ʾһ���м�¼������������Ϣ
     *                Map<String ,Map<String ,String>>.Map.key   ��ʾһ���ֶ�����
     *                Map<String ,Map<String ,String>>.Map.value ��ʾһ���ֶ�ֵ
     */
    public Map<String ,Map<String ,String>> getRows(String i_TableName ,List<String> i_Keys)
    {
        return this.core_getRows(i_TableName ,i_Keys.toArray(new String []{}));
    }
    
    
    
    /**
     * ��ȡ�������ݵ������ֶ���Ϣ
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     * @return        Map<String ,Map<String ,String>>.key       ��ʾһ��������
     *                Map<String ,Map<String ,String>>.Map       ��ʾһ���м�¼������������Ϣ
     *                Map<String ,Map<String ,String>>.Map.key   ��ʾһ���ֶ�����
     *                Map<String ,Map<String ,String>>.Map.value ��ʾһ���ֶ�ֵ
     */
    public Map<String ,Map<String ,String>> getRows(String i_TableName ,String ... i_Keys)
    {
        return this.core_getRows(i_TableName ,i_Keys);
    }
    
    
    
    /**
     * ��ȡ�������ݵ������ֶ���Ϣ
     * 
     * @param i_TableName  ������
     * @param i_Keys       ����ID--�ؼ��֡�ע�ⲻ��������
     * @return        Map<String ,Map<String ,String>>.key       ��ʾһ��������
     *                Map<String ,Map<String ,String>>.Map       ��ʾһ���м�¼������������Ϣ
     *                Map<String ,Map<String ,String>>.Map.key   ��ʾһ���ֶ�����
     *                Map<String ,Map<String ,String>>.Map.value ��ʾһ���ֶ�ֵ
     */
    private Map<String ,Map<String ,String>> core_getRows(String i_TableName ,String [] i_Keys)
    {
        return this.getRHash().getValues(this.makeRowKeys(i_TableName ,i_Keys));
    }
    
    
    
    /**
     * ��ʾ���е���������
     * 
     * @param i_TableName
     */
    public void showTableDatas(String i_TableName)
    {
        Map<String ,String>              v_RowKeys = this.getRHash().getValues(i_TableName);
        Map<String ,Map<String ,String>> v_Datas   = this.getRHash().getValues((List<String>)JavaHelp.toListKeys(v_RowKeys));
        
        for (String v_RowKey : v_RowKeys.keySet())
        {
            String v_RowTimestamp = v_RowKeys.get(v_RowKey);
            
            System.out.println("-- " + v_RowTimestamp + " | " + v_RowKey + " | " + v_Datas.get(v_RowKey));
        }
    }
    
    
    
    public List<String> getShardNames()
    {
        return this.shardInfoMap.getKeys();
    }
    
    
    
    /**
     * Ϊ�˰�ȫ��������ʹ��
     * 
     * @param i_Index
     * @return
     */
    @SuppressWarnings("unused")
    private String getShardName(int i_Index)
    {
        return this.shardInfoMap.getKey(i_Index);
    }
    
    
    
    /**
     * Ϊ�˰�ȫ��������ʹ��
     * 
     * @param i_ShardName
     * @return
     */
    @SuppressWarnings("unused")
    private int getShardNameIndex(String i_ShardName)
    {
        return this.shardInfoMap.getIndex(i_ShardName);
    }
    
    
    
    public int getShardSize()
    {
        return this.shardInfoMap.size();
    }
    
    
    /**
     * ����Jobs -- ����쳣�ļ�Ⱥ�Ƿ��ѻָ�����
     */
    private synchronized void startJobs()
    {
        if ( this.job == null )
        {
            this.job = new Job();
            
            this.job.setCode(        "JOB_Redis_ShardExceptionIsConnection");
            this.job.setName(        this.job.getName());
            this.job.setIntervalType(Job.$IntervalType_Minute);
            this.job.setIntervalLen( 1);
            this.job.setStartTime(   Date.getNowTime().getFull());
            this.job.setXid(         this.xjavaID);
            this.job.setMethodName(  "shardExceptionIsConnection");
        }
        
        if ( this.jobs == null )
        {
            this.jobs = new Jobs();
            this.jobs.addJob(this.job);
        }
        
        this.jobs.startup();
    }
    
    
    
    /**
     * �����쳣�ķֲ�ʱ��Ⱥ��Ϣ
     * 
     * ֻ���ڱ�������ģʽ
     * 
     * @param i_ShardName
     */
    protected synchronized void shardException(String i_ShardName)
    {
        if ( this.runMode != RunMode.$Backup )
        {
            return;
        }
        
        if ( i_ShardName == null )
        {
            return;
        }
        
        if ( this.shardInfoMap.containsKey(i_ShardName) )
        {
            this.exceptionShards.put(i_ShardName ,this.shardInfoMap.remove(i_ShardName));
            this.startJobs();
        }
    }
    
    
    
    /**
     * �����쳣�ķֲ�ʱ��Ⱥ�ѻָ�����
     * 
     * ֻ���ڱ�������ģʽ
     * 
     * @param i_ShardName
     */
    protected synchronized void shardConnection(String i_ShardName)
    {
        if ( this.runMode != RunMode.$Backup )
        {
            return;
        }
        
        if ( i_ShardName == null )
        {
            return;
        }
        
        if ( this.exceptionShards.containsKey(i_ShardName) )
        {
            this.shardInfoMap.put(i_ShardName ,this.exceptionShards.remove(i_ShardName));
        }
    }
    
    
    
    /**
     * ����쳣�ļ�Ⱥ�Ƿ��ѻָ�����
     * 
     * ֻ���ڱ�������ģʽ
     */
    public synchronized void shardExceptionIsConnection()
    {
        try
        {
            if ( JavaHelp.isNull(this.exceptionShards) )
            {
                return;
            }
            
            for (int i=this.exceptionShards.size() - 1; i >= 0; i--)
            {
                try
                {
                    Jedis v_Jedis = this.exceptionShards.get(i).createResource();
                    
                    if ( v_Jedis == null )
                    {
                        continue;
                    }
                    
                    String v_IsPONG = v_Jedis.ping();
                    if ( !JavaHelp.isNull(v_IsPONG) && "PONG".equals(v_IsPONG) )
                    {
                        this.shardConnection(this.exceptionShards.getKey(i));
                    }
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
            }
        }
        finally
        {
            if ( JavaHelp.isNull(this.exceptionShards) )
            {
                this.jobs.shutdown();
            }
        }
    }
    
    
    
    public RunMode getRunMode()
    {
        return runMode;
    }


    
    public void setRunMode(RunMode runMode)
    {
        if ( this.shardInfoMap.size() > 1 )
        {
            this.runMode = runMode;
        }
        else
        {
            if ( runMode != RunMode.$Backup )
            {
                throw new RuntimeException("ShardInfo size is 1. Only $Backup runmode.");
            }
        }
    }
    
    
    
    public boolean isKeyOrder()
    {
        return isKeyOrder;
    }

    
    
    public void setKeyOrder(boolean isKeyOrder)
    {
        this.isKeyOrder = isKeyOrder;
    }


    
    public RowKeyType getRowKeyType()
    {
        return rowKeyType;
    }


    
    public void setRowKeyType(RowKeyType rowKeyType)
    {
        this.rowKeyType = rowKeyType;
    }
    
    
    
    public String getXID()
    {
        return this.xjavaID;
    }
    
    
    
    public RedisKey getRKey()
    {
        return this.redisKey;
    }
    
    
    
    public RedisString getRString()
    {
        return this.redisString;
    }
    
    
    
    public RedisSet getRSet()
    {
        return this.redisSet;
    }
    
    
    
    public RedisHash getRHash()
    {
        return redisHash;
    }
    
    
    
    public RedisServer getRServer()
    {
        return redisServer;
    }
    
}
