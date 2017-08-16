package com.l2jhellas.mmocore.network;

import java.nio.channels.SocketChannel;

/**
 * @author KenM
 */
public interface IAcceptFilter
{
	public boolean accept(SocketChannel sc);
}