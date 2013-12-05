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

/**
 * The Class JcrValueWrapper.
 */
public class JcrValueWrapper {
	
	/** The log. */
	Logger log = LoggerFactory.getLogger(JcrValueWrapper.class);
	
	/** The value. */
	Value value;
	
	/** The locale. */
	Locale locale = Locale.getDefault();
	
	/** The throw exception. */
	boolean throwException = true;

	
	/**
	 * Instantiates a new jcr value wrapper.
	 *
	 * @param property the property
	 */
	public JcrValueWrapper(Value property) {
		this.value = property;
	}

	/**
	 * Instantiates a new jcr value wrapper.
	 *
	 * @param property the property
	 * @param locale the locale
	 */
	public JcrValueWrapper(Value property, Locale locale) {
		this.value = property;
		this.locale = locale;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	/**
	 * Instantiates a new jcr value wrapper.
	 *
	 * @param property the property
	 * @param locale the locale
	 * @param throwExcpetion the throw excpetion
	 */
	public JcrValueWrapper(Value property, Locale locale, boolean throwExcpetion) {
		this.value = property;
		this.locale = locale;
		this.throwException = throwExcpetion;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	/**
	 * Gets the long.
	 *
	 * @return the long
	 * @throws RepositoryException the repository exception
	 */
	public long getLong() throws RepositoryException {
		try {
			return value.getLong();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	/**
	 * Gets the double.
	 *
	 * @return the double
	 * @throws RepositoryException the repository exception
	 */
	public double getDouble() throws RepositoryException {
		try {
			return value.getDouble();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Gets the decimal.
	 *
	 * @return the decimal
	 * @throws RepositoryException the repository exception
	 */
	public BigDecimal getDecimal() throws RepositoryException {
		try {
			return value.getDecimal();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the calendar.
	 *
	 * @return the calendar
	 * @throws RepositoryException the repository exception
	 */
	public Calendar getCalendar() throws RepositoryException {
		try {
			return value.getDate();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 * @throws RepositoryException the repository exception
	 */
	public Date getDate() throws RepositoryException {
		try {
			return value.getDate().getTime();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	/**
	 * Gets the formatted date.
	 *
	 * @return the formatted date
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedDate() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date.
	 *
	 * @return the formatted short date
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium date.
	 *
	 * @return the formatted medium date
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	/**
	 * Gets the formatted long date.
	 *
	 * @return the formatted long date
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full date.
	 *
	 * @return the formatted full date
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDate() throws RepositoryException {
		try {
			return DateFormat.getDateInstance(DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	
	/**
	 * Gets the formatted short time.
	 *
	 * @return the formatted short time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium time.
	 *
	 * @return the formatted medium time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	/**
	 * Gets the formatted long time.
	 *
	 * @return the formatted long time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full time.
	 *
	 * @return the formatted full time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullTime() throws RepositoryException {
		try {
			return DateFormat.getTimeInstance(DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date with time.
	 *
	 * @return the formatted short date with time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date with short time.
	 *
	 * @return the formatted short date with short time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date with medium time.
	 *
	 * @return the formatted short date with medium time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date with long time.
	 *
	 * @return the formatted short date with long time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted short date with full time.
	 *
	 * @return the formatted short date with full time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedShortDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium date with time.
	 *
	 * @return the formatted medium date with time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium date with short time.
	 *
	 * @return the formatted medium date with short time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium date with medium time.
	 *
	 * @return the formatted medium date with medium time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted medium date with long time.
	 *
	 * @return the formatted medium date with long time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	/**
	 * Gets the formatted medium date with full time.
	 *
	 * @return the formatted medium date with full time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedMediumDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted long date with time.
	 *
	 * @return the formatted long date with time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted long date with short time.
	 *
	 * @return the formatted long date with short time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted long date with medium time.
	 *
	 * @return the formatted long date with medium time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted long date with long time.
	 *
	 * @return the formatted long date with long time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedLongDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted long date with full time.
	 *
	 * @return the formatted long date with full time
	 * @throws RepositoryException the repository exception
	 */
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

	/**
	 * Gets the formatted full date with time.
	 *
	 * @return the formatted full date with time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDateWithTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full date with short time.
	 *
	 * @return the formatted full date with short time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDateWithShortTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full date with medium time.
	 *
	 * @return the formatted full date with medium time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDateWithMediumTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full date with long time.
	 *
	 * @return the formatted full date with long time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDateWithLongTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the formatted full date with full time.
	 *
	 * @return the formatted full date with full time
	 * @throws RepositoryException the repository exception
	 */
	public String getFormattedFullDateWithFullTime() throws RepositoryException {
		try {
			return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, locale).format(value.getDate().getTime());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	/**
	 * Gets the iso8601 date.
	 *
	 * @return the iso8601 date
	 * @throws RepositoryException the repository exception
	 */
	public String getIso8601Date() throws RepositoryException {
		try {
			return ISO8601.fromCalendar(value.getDate());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the boolean.
	 *
	 * @return the boolean
	 * @throws RepositoryException the repository exception
	 */
	public boolean getBoolean() throws RepositoryException {
		try {
			return value.getBoolean();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
