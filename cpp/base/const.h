#ifndef CONST_H_
#define CONST_H_

/**
  * Ĭ����id��ռλ��
  */
#define DEF_SID		""

/**
 * Ĭ���¼���ռλ��
 */
#define DEF_EVENT	""

/**
 * Ĭ��Ԫ��Ϣ�ַ�����ռλ��
 */
#define DEF_META_STRING	""

/**
 * Ĭ�϶˿�
 * */
const int DEF_PORT = 8602;

/**
 * ��Э��رտ�ʼ����ȫ�رգ�
 */
const int CLOSE1000_PROTOCOL_CLOSE_STARTING = 1000;

/**
 * ��Э��ָ��ر�
 */
const int CLOSE1001_PROTOCOL_CLOSE = 1001;

/**
 * ��Э��Ƿ��ر�
 */
const int CLOSE1002_PROTOCOL_ILLEGAL = 1002;

/**
 * ���쳣�ر�
 */
const int CLOSE2001_ERROR = 2001;

/**
 * �������ر�
 */
const int CLOSE2002_RECONNECT = 2002;

/**
 * ���ʧ�ܹر�
 */
const int CLOSE2008_OPEN_FAIL = 2008;

/**
 * ���û������رգ�������������
 */
const int CLOSE2009_USER = 2009;


/**
 * ��ID�����������
 */
const int MAX_SIZE_SID = 64;

/**
 * �¼������������
 */
const int MAX_SIZE_EVENT = 512;

/**
 * Ԫ��Ϣ�������������
 */
const int MAX_SIZE_META_STRING = 4096;

/**
 * ���ݳ���������ƣ�Ҳ�Ƿ�Ƭ����������ƣ�
 */
const int MAX_SIZE_DATA = 1024 * 1024 * 16; //16m

/**
 * ֡�����������
 */
const int MAX_SIZE_FRAME = 1024 * 1024 * 17; //17m

/**
 * ��Ƭ������С����
 */
const int MIN_FRAGMENT_SIZE = 1024; //1k

/**
 * ������
 */
const int DEMANDS_ZERO = 0;

/**
 * ������
 */
const int DEMANDS_SINGLE = 1;

/**
 * ������
 */
const int DEMANDS_MULTIPLE = 2;

#endif