FROM clamav/clamav:1.4.3
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
