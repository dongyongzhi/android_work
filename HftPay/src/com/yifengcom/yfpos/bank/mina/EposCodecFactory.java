package com.yifengcom.yfpos.bank.mina;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.yifengcom.yfpos.bank.EposDecoder;
import com.yifengcom.yfpos.bank.EposEncoder;
public class EposCodecFactory implements ProtocolCodecFactory  {


		EposDecoder decoder;
		EposEncoder encoder;
		public EposCodecFactory(EposDecoder decoder, EposEncoder encoder){
			this.decoder = decoder;
			this.encoder = encoder;
		}
		
		@Override
		public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
			// TODO Auto-generated method stub
			return decoder;
		}
		
		@Override
		public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
			// TODO Auto-generated method stub
			return (ProtocolEncoder) encoder;
		}

}
