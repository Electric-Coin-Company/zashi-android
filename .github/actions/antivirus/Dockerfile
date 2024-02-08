FROM clamav/clamav:1.3.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
