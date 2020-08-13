-- SPDX-FileCopyrightText: 2020 Sveriges Television AB
--
-- SPDX-License-Identifier: EUPL-1.2

ALTER TABLE viquse_job ADD progress_callback_uri VARCHAR(512);
ALTER TABLE viquse_job ADD progress INTEGER;
