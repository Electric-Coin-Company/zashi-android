FROM clamav/clamav:1.1.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
