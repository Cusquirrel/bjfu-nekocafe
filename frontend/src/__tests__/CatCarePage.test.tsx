import { fireEvent, screen, waitFor } from '@testing-library/react';
import { describe, expect, test } from 'vitest';
import App from '../App';
import { installMockApi } from '../test/mockApi';
import { renderWithClient } from '../test/render';

describe('Cat care page', () => {
  test('displays cat status and submits health record with value field', async () => {
    const fetchMock = installMockApi();
    renderWithClient(<App />);

    fireEvent.click(await screen.findByText('猫咪照护'));
    expect(await screen.findByText(/年糕/)).toBeInTheDocument();
    expect(screen.getAllByText('休息中').length).toBeGreaterThan(0);

    fireEvent.click(screen.getAllByText('+ 健康记录')[0]);
    fireEvent.change(screen.getByDisplayValue('选择记录类型...'), { target: { value: 'HEALTH_CHECK' } });
    fireEvent.change(screen.getByPlaceholderText('备注信息...'), { target: { value: '精神良好' } });
    fireEvent.click(screen.getByText('提交'));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/cats/1/health-records', expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ recordType: 'HEALTH_CHECK', value: '精神良好', recordedBy: 'catkeeper' }),
      }));
    });
  });
});
