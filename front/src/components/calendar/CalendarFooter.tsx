import React from 'react';
import {View} from 'react-native';

interface CalendarFooterProps {
  month: Date;
  renderFooter: (props: any) => React.ReactNode;
}

function CalendarFooter({month, renderFooter}: CalendarFooterProps) {
  return <View>{renderFooter({month})}</View>;
}

export default CalendarFooter; 