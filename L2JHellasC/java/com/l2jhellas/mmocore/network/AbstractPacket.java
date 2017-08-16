package com.l2jhellas.mmocore.network;

import java.nio.ByteBuffer;

/**
 * @author KenM
 * @param <T>
 */
public abstract class AbstractPacket<T extends MMOClient<?>>
{
	protected ByteBuffer _buf;
	
	T _client;
	
	public final T getClient()
	{
		return _client;
	}
}