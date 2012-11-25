package org.liveSense.core.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Binary;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import net.entropysoft.transmorph.ConverterException;
import net.entropysoft.transmorph.DefaultConverters;
import net.entropysoft.transmorph.Transmorph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robson
 */
public class GenericValue extends ArrayList<Value> implements Serializable {

	private static final Transmorph converter = new Transmorph(new DefaultConverters());
	private static Logger log = LoggerFactory.getLogger(GenericValue.class);

	public static GenericValue getGenericValueFromObject(final Object inValue) {
		if (inValue instanceof Value) {
			return new GenericValue((Value) inValue);
		} else if (inValue instanceof Value[]) {
			return new GenericValue((Value[]) inValue);
		} else if (inValue instanceof ArrayList) {
			try {
				return new GenericValue((ArrayList<Value>) inValue);
			} catch (ClassCastException ex) {
				throw new UnsupportedOperationException("The ArrayList have to contain Value type elements");
			}
		} else {

			Value value = new Value() {
				@Override
				public String getString() throws ValueFormatException, IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, String.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to String " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to String " + inValue);
					}
				}

				@Override
				public InputStream getStream() throws IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, InputStream.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to InputStream " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to InputStream " + inValue);
					}
				}

				@Override
				public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, Long.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to Long " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to Long " + inValue);
					}
				}

				@Override
				public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, Double.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to Double " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to Double " + inValue);
					}
				}

				@Override
				public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, Calendar.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to Calendar " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to Calendar " + inValue);
					}
				}

				@Override
				public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException {
					try {
						return converter.convert(inValue, Boolean.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to Boolean " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to Boolean " + inValue);
					}
				}

				@Override
				public int getType() {
					if (inValue instanceof String) {
						return PropertyType.STRING;
					} else if (inValue instanceof Date) {
						return PropertyType.DATE;
					} else if (inValue instanceof Calendar) {
						return PropertyType.DATE;
					} else if (inValue instanceof InputStream) {
						return PropertyType.BINARY;
					} else if (inValue instanceof Double) {
						return PropertyType.DOUBLE;
					} else if (inValue instanceof Long) {
						return PropertyType.LONG;
					} else if (inValue instanceof Boolean) {
						return PropertyType.BOOLEAN;
					} else {
						return PropertyType.STRING;
						//return PropertyType.UNDEFINED;
					}
				}

				@Override
				public Binary getBinary() throws RepositoryException {
					return new Binary() {

						InputStream is;
						int offs = 0;
						private InputStream getStreamFromValue() {
							try {
								return converter.convert(inValue, InputStream.class);
							} catch (ConverterException ex) {
								log.warn("Cannot convert to InputStream " + inValue, ex);
								throw new UnsupportedOperationException("Cannot convert to InputStream " + inValue);
							}
						}

						@Override
						public InputStream getStream() throws RepositoryException {
							if (is == null) is = getStreamFromValue();
							return is;
						}

						@Override
						public int read(byte[] bytes, long l) throws IOException, RepositoryException {
							if (is == null) is = getStreamFromValue();
							int readed =  is.read(bytes, offs, (int)l);
							offs+=readed;
							return readed;
						}

						@Override
						public long getSize() throws RepositoryException {
							if (is == null) is = getStreamFromValue();
							try {
								return is.available();
							} catch (IOException ex) {
								throw new RepositoryException(ex.getMessage());
							}
						}

						@Override
						public void dispose() {
							if (is == null) is = getStreamFromValue();
							try {
								is.close();
							} catch (IOException ex) {
								log.error(ex.getMessage());
							}

						}
					};
				}

				@Override
				public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
					try {
						return converter.convert(inValue, BigDecimal.class);
					} catch (ConverterException ex) {
						log.warn("Cannot convert to BigDecimal " + inValue, ex);
						throw new UnsupportedOperationException("Cannot convert to BigDecimal " + inValue);
					}
				}

			};
			return new GenericValue(value);
		}

	}

	public boolean isMultiValue() {
		if (size() > 1) {
			return true;
		}
		return false;
	}

	public GenericValue() {
		super();
	}

	public GenericValue(Value value) {
		add(value);
	}

	public GenericValue(Value[] values) {
		for (int i = 0; i < values.length; i++) {
			add(values[i]);
		}
	}

	public GenericValue(ArrayList<Value> values) {
		for (Value value : values) {
			add(value);
		}
	}

	public GenericValue(Property value) throws RepositoryException {
		try {
			add(value.getValue());
		} catch (ValueFormatException ex) {
			try {
				Value[] values = value.getValues();
				for (int i = 0; i < values.length; i++) {
					add(values[i]);
				}
			} catch (ValueFormatException ex1) {
				throw new RepositoryException("UNKNOWN ERROR: Both of Value.getValue() and Value.getValues() returns with Exception");
			}
		}
	}

	public Value[] getValues() {
		try {
			return converter.convert(this, Value[].class);
		} catch (ConverterException ex) {
			log.warn("Cannot convert GenericValue to Value[]", ex);
			throw new UnsupportedOperationException("Cannot convert GenericValue to Value[]");
		}
	}

	private Object getValueObject(Value value) throws RepositoryException {
		try {
			switch (value.getType()) {
				case PropertyType.BINARY:
					return value.getString();
				case PropertyType.BOOLEAN:
					return new Boolean(value.getBoolean());
				case PropertyType.DATE:
					return value.getDate();
				case PropertyType.DOUBLE:
					return new Double(value.getDouble());
				case PropertyType.LONG:
					return new Long(value.getLong());
				case PropertyType.NAME:
					return value.getString();
				case PropertyType.PATH:
					return value.getString();
				case PropertyType.REFERENCE:
					return value.getString();
				case PropertyType.STRING:
					return value.getString();
				case PropertyType.UNDEFINED:
					return value.getString();
				default:
					return null;
			}
		} catch (ValueFormatException e) {
			return "Invalid value format";
		} catch (RepositoryException e) {
			log.warn("Repoitory error", e);
			throw e;
		}
	}

	public Object getGenericObject() throws RepositoryException {
		if (this.size() < 1) {
			return null;
		}
		if (this.isMultiValue()) {
			ArrayList<Object> ret = new ArrayList<Object>();
			for (int i = 0; i < this.size(); i++) {
				ret.add(getValueObject(this.get(i)));
			}
			return ret;
		} else {
			return getValueObject(this.get(0));
		}
	}

	public Value get() {
		if (this.size() < 1) {
			return null;
		} else {
			return this.get(0);
		}
	}

}
