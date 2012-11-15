package org.liveSense.core.wrapper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.liveSense.core.ISO8601;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrValueWrapper {
	Logger log = LoggerFactory.getLogger(JcrValueWrapper.class);
	
	Value value;
	Locale locale = Locale.getDefault();
	boolean throwException = true;

	
	public JcrValueWrapper(Value property) {
		this.value = property;
	}

	public JcrValueWrapper(Value property, Locale locale) {
		this.value = property;
		this.locale = locale;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	public JcrValueWrapper(Value property, Locale locale, boolean throwExcpetion) {
		this.value = property;
		this.locale = locale;
		this.throwException = throwExcpetion;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	public long getLong() throws RepositoryException {
		try {
			return value.getLong();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public double getDouble() throws RepositoryException {
		try {
			return value.getDouble();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return Double.NEGATIVE_INFINITY;
	}

	public BigDecimal getDecimal() throws RepositoryException {
		try {
			return value.getDecimal();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public Calendar getCalendar() throws RepositoryException {
		try {
			return value.getDate();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public Date getDate() throws RepositoryException {
		try {
			return value.getDate().getTime();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	public String getFormattedDate() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedShortDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	public String getFormattedLongDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	
	public String getFormattedShortTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	public String getFormattedLongTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormatteShortDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormatteShortDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormatteShortDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormatteShortDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormatteShortDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedMediumDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	public String getFormattedMediumDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedLongDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedLongDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedLongDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedLongDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedLongDateWithFullTime() throws RepositoryException {
		try {
			if (value == null) {
				log.error("Value is null");
			} else if (value.getDate() == null) {
				log.error("Value.getDate() is null");
			} else if (value.getDate().getTime() == null) {
				log.error("Value.getDate().getTime() is null");
			}
			
			if (locale == null) {
				log.error("Locale is null");
			}
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getFormattedFullDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	public String getIso8601Date() throws RepositoryException {
		try {
			return ISO8601.fromCalendar(value.getDate());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public boolean getBoolean() throws RepositoryException {
		try {
			return value.getBoolean();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return false;
	}

	@Override
	public String toString()  {
		try {
			switch (value.getType()) {
				case PropertyType.BINARY:
					return value.getString();
				case PropertyType.BOOLEAN:
					return new Boolean(value.getBoolean()).toString();
				case PropertyType.DATE:
					return getFormattedDate();
				case PropertyType.DOUBLE:
					return new Double(getDouble()).toString();
				case PropertyType.LONG:
					return new Long(getLong()).toString();
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
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IllegalStateException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
