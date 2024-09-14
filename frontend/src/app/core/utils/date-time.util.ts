import moment from 'moment';

export default class DateTimeUtils {

    /**
     * Returns date with no time attached.
     * @param date
     * @returns {Date}
     */
    static dateWithoutTime(date: Date): Date {
        const d = new Date(date);
        d.setHours(0, 0, 0, 0);
        return d;
    }

    /**
     * Returns date with UTC.
     * @param date
     * @returns {Date}
     */
    static dateWithoutUTC(value: string): Date {
        const d = new Date(moment.utc(value).format('MM/DD/YYYY'));
        return d;
    }

    /**
     * Returns date with no time attached.
     * @param date
     * @returns {Date}
     */
    static convertObjectToDate(object: { year: number, month: number, day: number }) {
        const year = object.year;
        const month = object.month;
        const day = object.day;
        return month + '/' + day + '/' + year;
    }

    /**
     * Returns true/false if date is past the threshold of allowed days.
     *
     * @param {Date} date
     * @param {number} daysThreshold
     * @returns {boolean}
     */
    static determineReadOnlyBasedOnDate(date: Date, daysThreshold: number) {
        const now = new Date();
        const startDateMinusOne = new Date(date);
        startDateMinusOne.setDate(startDateMinusOne.getDate() - daysThreshold);
        return startDateMinusOne < now;
    }

    /**
     * Increments date by number of days.
     *
     * @param {Date} date
     * @param {number} days
     * @returns {Date}
     */
    static incrementDate(date: Date, days: number) {
        const newDate = new Date(date);
        newDate.setDate(newDate.getDate() + days);
        return newDate;
    }

    /**
     * Decrements date by number of days.
     *
     * @param {Date} date
     * @param {number} days
     * @returns {Date}
     */
    static decrementDate(date: Date, days: number) {
        const newDate = new Date(date);
        newDate.setDate(newDate.getDate() - days);
        return newDate;
    }

    /**
     * Returns valid date object from string YYYY-MM-DD
     * @param {string} dateString
     * @returns {Date}
     */
    static dateFromDateStringYYYYMMDDD(dateString: string): Date {
        // tslint:disable-next-line: radix
        const year = parseInt(dateString.substring(0, 4));
        // tslint:disable-next-line: radix
        const month = parseInt(dateString.substring(5, 7));
        // tslint:disable-next-line: radix
        const day = parseInt(dateString.substring(8, 10));
        return new Date(year, month - 1, day, 0, 0, 0, 0);
    }

    /**
     * Check if obj is date format, if not, convert to Date;
     * @param {Object} obj
     * @returns {Date}
     */
    static checkDateAndConvert(obj: Object): Date {
        if (obj instanceof Date) {
            return obj;
        }
        return this.dateFromDateStringYYYYMMDDD(obj as string);
    }

    /**
     * Formats date for excel file name.
     * @returns {string}
     */
    static getDateForTitle(): string {
        const date = new Date();
        const day = date.getDate();
        const monthIndex = date.getMonth();
        const year = date.getFullYear();
        return (monthIndex + 1) + '_' + day + '_' + year;
    }

    /**
     * Shorthand function to compare dates without time.
     * @param {Date} date1
     * @param {Date} date2
     * @returns {boolean}
     */
    static areDatesEqualWithoutTime(date1: Date, date2: Date): boolean {
        return this.dateWithoutTime(date1).getTime() === this.dateWithoutTime(date2).getTime();
    }

    /**
     * Checks if scheduled date not in offer date range.
     * @param {Date} scheduledDate
     * @param {Date} offerStartDate
     * @param {Date} offerEndDate
     * @returns {boolean}
     */
    static isScheduledDateOutsideOfferDateRange(scheduledDate: Date, offerStartDate: Date, offerEndDate: Date): boolean {
        const scheduleDateWithoutTime = this.dateWithoutTime(scheduledDate);
        const offerStartDateWithoutTime = this.dateWithoutTime(offerStartDate);
        const offerEndDateWithoutTime = this.dateWithoutTime(offerEndDate);
        return scheduleDateWithoutTime < offerStartDateWithoutTime || scheduleDateWithoutTime > offerEndDateWithoutTime;
    }

    /**
     * Check to see if schedule date is before or on start date.
     * @param {Date} scheduledDate
     * @param {Date} startDate
     * @returns {boolean}
     */
    static isScheduledDateLessThanOrEqualStart(scheduledDate: Date, startDate: Date): boolean {
        const scheduleDateWithoutTime = this.dateWithoutTime(scheduledDate);
        const startDateWithoutTime = this.dateWithoutTime(startDate);
        return scheduleDateWithoutTime <= startDateWithoutTime;
    }

    /**
     * Check to see if schedule date is before or on start date.
     * @param {Date} scheduledDate
     * @param {Date} startDate
     * @returns {boolean}
     */
    static isScheduledDateLessThanStart(scheduledDate: Date, startDate: Date): boolean {
        const scheduleDateWithoutTime = this.dateWithoutTime(scheduledDate);
        const startDateWithoutTime = this.dateWithoutTime(startDate);
        return scheduleDateWithoutTime < startDateWithoutTime;
    }

    /**
     * Check to see if schedule date is after or on start date.
     * @param {Date} scheduledDate
     * @param {Date} offerEndDate
     * @returns {boolean}
     */
    static isScheduledDateAfterOrEqualEndDate(scheduledDate: Date, offerEndDate: Date) {
        const scheduleDateWithoutTime = this.dateWithoutTime(scheduledDate);
        const startDateWithoutTime = this.dateWithoutTime(offerEndDate);
        return scheduleDateWithoutTime >= startDateWithoutTime;
    }

    static getShortDateDescription(dateFull: string) {
        const DATES_FULL = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
        const DATES_SHORT = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
        return DATES_SHORT[DATES_FULL.indexOf(dateFull)];
    }
}