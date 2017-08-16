package com.l2jhellas.mmocore.network;

/**
 * @author KenM
 * @param <T>
 */
public interface IClientFactory<T extends MMOClient<?>>
{
	public T create(final MMOConnection<T> con);
}